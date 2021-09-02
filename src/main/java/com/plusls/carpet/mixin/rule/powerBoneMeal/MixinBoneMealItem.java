package com.plusls.carpet.mixin.rule.powerBoneMeal;


import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.powerfulBoneMeal.Grow;
import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class MixinBoneMealItem {

    @Inject(method = "useOnFertilizable", at = @At(value = "RETURN"), cancellable = true)
    private static void postUseOnFertilizable(ItemStack stack, World world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValueZ() && world instanceof ServerWorld && PcaSettings.powerfulBoneMeal) {
            BlockState blockState = world.getBlockState(pos);
            info.setReturnValue(Grow.grow(stack, world, pos, blockState.getBlock()));
        }
    }
}
