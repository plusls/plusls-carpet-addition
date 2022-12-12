package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PcaSettings;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    private static final RegistryEntryLookup<Biome> pca$lookup = BuiltinRegistries.createWrapperLookup().createRegistryLookup().getOrThrow(RegistryKeys.BIOME);

    @ModifyVariable(method = "getSpawnEntries",
            at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private static RegistryEntry<Biome> modifyBiome(RegistryEntry<Biome> biomeEntry) {
        if (PcaSettings.spawnBiome != PcaSettings.PCA_SPAWN_BIOME.DEFAULT) {

            if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.DESERT) {
                biomeEntry = pca$lookup.getOrThrow(BiomeKeys.DESERT);
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.PLAINS) {
                biomeEntry = pca$lookup.getOrThrow(BiomeKeys.PLAINS);
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.THE_END) {
                biomeEntry = pca$lookup.getOrThrow(BiomeKeys.THE_END);
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.NETHER_WASTES) {
                biomeEntry = pca$lookup.getOrThrow(BiomeKeys.NETHER_WASTES);
            }
        }
        return biomeEntry;
    }
}
