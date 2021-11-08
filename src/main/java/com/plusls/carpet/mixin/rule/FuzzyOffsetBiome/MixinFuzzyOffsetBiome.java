package com.plusls.carpet.mixin.rule.FuzzyOffsetBiome;

import com.plusls.carpet.PcaSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;

@Mixin(VoronoiBiomeAccessType.class)
public class MixinFuzzyOffsetBiome {

	@Inject(method = "getBiome", at = @At("HEAD"), cancellable = true)
	public void injected(long seed, int x, int y, int z, BiomeAccess.Storage storage,
			CallbackInfoReturnable<Biome> cir) {
		if (PcaSettings.fuzzyOffsetBiome) {
			return;
		}
		cir.setReturnValue(storage.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2));
	}
}
