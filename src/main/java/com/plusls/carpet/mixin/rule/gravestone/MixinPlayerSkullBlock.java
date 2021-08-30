package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.util.rule.gravestone.DeathInfo;
import com.plusls.carpet.util.rule.gravestone.MySkullBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerSkullBlock.class)
public abstract class MixinPlayerSkullBlock extends SkullBlock {
    protected MixinPlayerSkullBlock(SkullType skullType, Settings settings) {
        super(skullType, settings);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        if (world.isClient()) {
            return;
        }
        if (blockEntity instanceof MySkullBlockEntity graveEntity) {
            DeathInfo deathInfo = graveEntity.getDeathInfo();
            if (deathInfo == null) {
                super.afterBreak(world, player, pos, state, blockEntity, stack);
            } else {
                player.incrementStat(Stats.MINED.getOrCreateStat(this));
                player.addExhaustion(0.005F);
                // Drop item
                for(ItemStack itemStack : deathInfo.inventory.clearToList()) {
                    dropStack(world, pos, itemStack);
                }

                // Drop xp
                int xp = deathInfo.xp;
                while (xp > 0) {
                    int spawnedXp = ExperienceOrbEntity.roundToOrbSize(xp);
                    xp -= spawnedXp;
                    world.spawnEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), spawnedXp));
                }
            }
        }
    }
}
