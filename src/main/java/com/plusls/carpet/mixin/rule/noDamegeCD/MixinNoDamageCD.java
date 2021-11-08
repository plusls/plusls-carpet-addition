package com.plusls.carpet.mixin.rule.noDamegeCD;

import com.plusls.carpet.PcaSettings;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class MixinNoDamageCD {

	@Shadow
	protected float lastDamageTaken;

	@Redirect(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;lastDamageTaken:F", opcode = Opcodes.GETFIELD))
	public float injected(LivingEntity lEntity) {

		if (PcaSettings.noDamegeCD) {
			return 0.0f;
		}
		return lastDamageTaken;

	}
}
