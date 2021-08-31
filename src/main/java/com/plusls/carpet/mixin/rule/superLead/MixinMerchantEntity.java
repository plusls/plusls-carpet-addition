package com.plusls.carpet.mixin.rule.superLead;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.Npc;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.Merchant;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantEntity.class)
public abstract class MixinMerchantEntity extends PassiveEntity implements InventoryOwner, Npc, Merchant {
    protected MixinMerchantEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashedBy", at = @At(value = "RETURN"), cancellable = true)
    private void postCanBeLeashedBy(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (PcaSettings.superLead) {
            cir.setReturnValue(!isLeashed());
        }
    }
}
