package com.plusls.carpet.mixin.rule.quickLeafDecay;

import com.plusls.carpet.PcaSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LeavesBlock.class)
public abstract class MixinLeavesBlock extends Block {

    public MixinLeavesBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "onScheduledTick", at = @At("RETURN"))
    private void postScheduledTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (PcaSettings.quickLeafDecay) {
            this.onRandomTick(state, world, pos, random);
        }
    }
}
