package com.plusls.carpet.mixin.rule.railNoBrokenByFluid;

import com.plusls.carpet.PcaSettings;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFluid.class)
public abstract class MixinFlowableFluid extends Fluid {
    @Inject(method = "canFill", at = @At(value = "RETURN"), cancellable = true)
    private void checkRail(BlockView world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (PcaSettings.railNoBrokenByFluid && cir.getReturnValue() && state.getBlock() instanceof AbstractRailBlock) {
            cir.setReturnValue(false);
        }
    }
}
