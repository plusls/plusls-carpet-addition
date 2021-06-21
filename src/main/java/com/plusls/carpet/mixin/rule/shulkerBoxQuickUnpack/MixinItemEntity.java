package com.plusls.carpet.mixin.rule.shulkerBoxQuickUnpack;

import com.plusls.carpet.PcaSettings;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 0))
    private void shulkerBoxItemUnpack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.world.isClient()) {
            return;
        }
        if (!PcaSettings.shulkerBoxQuickUnpack) {
            return;
        }
        ItemStack itemStack = getStack();
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock) {
            NbtCompound lv = itemStack.getTag();
            if (lv != null) {
                NbtList itemList = lv.getCompound("BlockEntityTag").getList("Items", 10);
                dropItems(itemList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt));
            }
        }
    }

    public void dropItems(Stream<ItemStack> stream) {
        if (this.world.isClient())
            return;
        stream.forEach(itemStack -> this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack)));
    }
}
