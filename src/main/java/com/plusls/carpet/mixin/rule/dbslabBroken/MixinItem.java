package com.plusls.carpet.mixin.rule.dbslabBroken;

import com.plusls.carpet.PcaSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.enums.SlabType;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinItem {

    @Shadow
    private MinecraftClient client;

    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
    public void canMine(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!PcaSettings.separateSlabBreaking) {
            return;
        }
        ClientWorld world = client.world;
        BlockState state = world.getBlockState(pos);
        if (!(((ClientPlayerInteractionManager) (Object) this).getCurrentGameMode().isSurvivalLike())) {
            return;
        }
        if(!client.player.canHarvest(state)){
            return;
        }
        if ((state.getBlock() instanceof SlabBlock) && (state.get(SlabBlock.TYPE) == SlabType.DOUBLE)) {
            cir.setReturnValue(false);
        }
    }
}