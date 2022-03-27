package com.plusls.carpet.mixin.rule.creativePlayerNoDirectKillArmorStand;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmorStandEntity.class)
public abstract class MixinArmorStandEntity extends LivingEntity {
    protected MixinArmorStandEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isSourceCreativePlayer()Z", ordinal = 0))
    private boolean redirectIsSourceCreativePlayer(DamageSource instance) {
        if (!this.world.isClient() && PcaSettings.creativePlayerNoDirectKillArmorStand) {
            return false;
        }
        return instance.isSourceCreativePlayer();
    }
}
