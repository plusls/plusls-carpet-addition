package com.plusls.carpet.mixin.rule.spawnBiome;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Redirect(method = "method_8664",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getEntitySpawnList(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;", ordinal = 0))
    private static List<Biome.SpawnEntry> modifyBiome0(ChunkGenerator<?> chunkGenerator, EntityCategory category, BlockPos pos) {
        return modifyBiome(chunkGenerator, category, pos);
    }

    @Redirect(method = "method_8659",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getEntitySpawnList(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;", ordinal = 0))
    private static List<Biome.SpawnEntry> modifyBiome1(ChunkGenerator<?> chunkGenerator, EntityCategory category, BlockPos pos) {
        return modifyBiome(chunkGenerator, category, pos);
    }

    @Redirect(method = "populateEntities",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/Biome;getEntitySpawnList(Lnet/minecraft/entity/EntityCategory;)Ljava/util/List;", ordinal = 0))
    private static List<Biome.SpawnEntry> modifyBiome2(Biome biome, EntityCategory category) {
        if (PcaSettings.spawnBiome != PcaSettings.PCA_SPAWN_BIOME.DEFAULT) {
            if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.DESERT) {
                biome = Biomes.DESERT;
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.PLAINS) {
                biome = Biomes.DESERT;
            }
        }
        return biome.getEntitySpawnList(category);
    }


    private static List<Biome.SpawnEntry> modifyBiome(ChunkGenerator<?> chunkGenerator, EntityCategory category, BlockPos pos) {
        if (PcaSettings.spawnBiome != PcaSettings.PCA_SPAWN_BIOME.DEFAULT) {
            if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.DESERT) {
                return Biomes.DESERT.getEntitySpawnList(category);
            } else if (PcaSettings.spawnBiome == PcaSettings.PCA_SPAWN_BIOME.PLAINS) {
                return Biomes.PLAINS.getEntitySpawnList(category);
            }
        }
        return chunkGenerator.getEntitySpawnList(category, pos);
    }

}
