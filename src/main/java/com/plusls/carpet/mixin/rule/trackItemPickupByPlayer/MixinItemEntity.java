package com.plusls.carpet.mixin.rule.trackItemPickupByPlayer;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    @Shadow public abstract void tick();

    private boolean pickuped = false;

    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }
    @Shadow
    public abstract void setStack(ItemStack stack);

    @Inject(method = "tick", at =@At(value = "HEAD"), cancellable = true)
    private void prevTick(CallbackInfo ci) {
        if (PcaSettings.trackItemPickupByPlayer && pickuped) {
            ci.cancel();
        }
    }
    @Inject(method = "onPlayerCollision", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"), cancellable = true)
    private void checkPickup(PlayerEntity player, CallbackInfo ci) {
        if (PcaSettings.trackItemPickupByPlayer) {
            pickuped = true;
            this.setStack(new ItemStack(Items.BARRIER));
            this.setNoGravity(true);
            this.noClip = true;
            this.setVelocity(0, 0, 0);
            ci.cancel();
        }
    }

}
