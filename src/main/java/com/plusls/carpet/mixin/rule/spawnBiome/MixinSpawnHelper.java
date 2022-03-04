package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PcaSettings;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @ModifyVariable(method = "getSpawnEntries",
            at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private static RegistryEntry<Biome> modifyBiome(RegistryEntry<Biome> biomeEntry) {
        if (PcaSettings.spawnBiome != PcaSettings.PCA_SPAWN_BIOME.DEFAULT) {
            if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.DESERT) {
                biomeEntry = RegistryEntry.of(BuiltinRegistries.BIOME.get(BiomeKeys.DESERT));
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.PLAINS) {
                // BuiltinBiomes
                biomeEntry = RegistryEntry.of(BuiltinRegistries.BIOME.get(BiomeKeys.PLAINS));
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.THE_END) {
                biomeEntry = RegistryEntry.of(BuiltinRegistries.BIOME.get(BiomeKeys.THE_END));
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.NETHER_WASTES) {
                biomeEntry = RegistryEntry.of(BuiltinRegistries.BIOME.get(BiomeKeys.NETHER_WASTES));
            }
        }
        return biomeEntry;
    }
}
