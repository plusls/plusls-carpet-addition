package com.plusls.carpet.mixin.rule.FuzzyOffsetBiome;

import com.plusls.carpet.PcaSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;

@Mixin(HorizontalVoronoiBiomeAccessType.class)
public class MixinConstantColumnBiome {

	@Inject(method = "getBiome", at = @At("HEAD"), cancellable = true)
	public void injected(long seed, int x, int y, int z, BiomeAccess.Storage storage,
			CallbackInfoReturnable<Biome> cir) {
		if (PcaSettings.constantColumnBiome) {
			return;
		}
		cir.setReturnValue(VoronoiBiomeAccessType.INSTANCE.getBiome(seed, x, y, z, storage));
	}
}
