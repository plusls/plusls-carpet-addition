package com.plusls.carpet.mixin.rule.noDamegeCD;

import com.plusls.carpet.PcaSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
@Mixin(PlayerEntity.class)
public class MixinNoPlayerAttackCD {

	@Inject(method ="getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
	public void injected(float z,CallbackInfoReturnable<Float> cir) {
		if(PcaSettings.noPlayerAttackCD){
			cir.setReturnValue(1.0f);
		}
    }
}
