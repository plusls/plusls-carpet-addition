package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.PcaSettings;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public abstract class MixinFlowableFluid extends Fluid {
    @Inject(method = "canFill", at = @At(value = "RETURN"), cancellable = true)
    private void checkRail(BlockView world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && state.getBlock() instanceof PlayerSkullBlock) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                NbtCompound nbt = blockEntity.writeNbt(new NbtCompound());
                if (nbt.contains("DeathInfo")) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}