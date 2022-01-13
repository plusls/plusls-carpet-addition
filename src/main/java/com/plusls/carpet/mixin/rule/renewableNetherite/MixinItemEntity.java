package com.plusls.carpet.mixin.rule.renewableNetherite;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Nullable
    private static ItemStack getNetheriteResult(ItemStack stack) {
        Item newItem;
        if (Items.DIAMOND_SWORD.equals(stack.getItem())) {
            newItem = Items.NETHERITE_SWORD;
        } else if (Items.DIAMOND_SHOVEL.equals(stack.getItem())) {
            newItem = Items.NETHERITE_SHOVEL;
        } else if (Items.DIAMOND_PICKAXE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_PICKAXE;
        } else if (Items.DIAMOND_AXE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_AXE;
        } else if (Items.DIAMOND_HOE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_HOE;
        } else if (Items.DIAMOND_HELMET.equals(stack.getItem())) {
            newItem = Items.NETHERITE_HELMET;
        } else if (Items.DIAMOND_CHESTPLATE.equals(stack.getItem())) {
            newItem = Items.NETHERITE_CHESTPLATE;
        } else if (Items.DIAMOND_LEGGINGS.equals(stack.getItem())) {
            newItem = Items.NETHERITE_LEGGINGS;
        } else if (Items.DIAMOND_BOOTS.equals(stack.getItem())) {
            newItem = Items.NETHERITE_BOOTS;
        } else {
            newItem = null;
        }
        if (newItem == null) {
            return null;
        }
        ItemStack ret = new ItemStack(newItem);
        NbtCompound nbtCompound = stack.getNbt();

        if (nbtCompound != null) {
            ret.setNbt(nbtCompound.copy());
            ret.setDamage(ret.getMaxDamage() - 1);
        }
        return ret;
    }

    @Shadow
    public abstract ItemStack getStack();


    @Shadow
    public abstract Packet<?> createSpawnPacket();


    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;discard()V", ordinal = 0))
    private void checkDiamondEquip(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!PcaSettings.renewableNetheriteEquip || this.world.isClient) {
            return;
        }
        ServerWorld world = (ServerWorld) this.world;
        if (source == DamageSource.LAVA && world.getRegistryKey() == World.NETHER) {
            ItemStack stack = getStack();
            if (!stack.isEmpty() && stack.getMaxDamage() - stack.getDamage() == 1) {
                Item item = stack.getItem();
                if ((item instanceof ArmorItem && ((ArmorItem) item).getMaterial() == ArmorMaterials.DIAMOND) ||
                        item instanceof ToolItem && ((ToolItem) item).getMaterial() == ToolMaterials.DIAMOND) {
                    ItemStack newItemStack = getNetheriteResult(stack);
                    if (newItemStack != null) {
                        world.spawnEntity(new ItemEntity(world, this.getX(), this.getY(), this.getZ(), newItemStack));
                    }
                }
            }
        }
    }
}
