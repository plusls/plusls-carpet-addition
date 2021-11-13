package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Redirect(method = "getSpawnEntries",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getEntitySpawnList(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/collection/Pool;"))
    private static Pool<SpawnSettings.SpawnEntry> modifyBiome(ChunkGenerator chunkGenerator, Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (PcaSettings.spawnBiome != PcaSettings.PCA_SPAWN_BIOME.DEFAULT) {
            if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.DESERT) {
                biome = BuiltinRegistries.BIOME.get(BiomeKeys.DESERT);
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.PLAINS) {
                // BuiltinBiomes
                biome = BuiltinRegistries.BIOME.get(BiomeKeys.PLAINS);
            }
        }
        return chunkGenerator.getEntitySpawnList(biome, accessor, group, pos);
    }
}
