package com.plusls.carpet.mixin.rule.flippingTotemOfUndying;

import carpet.CarpetSettings;
import carpet.helpers.BlockRotator;
import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.flippingTotemOfUndying.FlipCooldown;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRotator.class)
public class MixinBlockRotator {
    private static boolean player_holds_totemOfUndying_mainhand(PlayerEntity player) {
        return player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING;
    }

    @Inject(method = "flipBlockWithCactus", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void postFlipBlockWithCactus(BlockState state, World world, PlayerEntity player, Hand hand, BlockHitResult hit,
                                                CallbackInfoReturnable<Boolean> cir) {

        // 不知道为什么 同一 gt 内会收到 2 个包
        // it works
        if (!cir.getReturnValue() && PcaSettings.flippingTotemOfUndying &&
                world.getTime() != FlipCooldown.getCoolDown(player)) {
            // 能修改世界且副手为空
            if (!player.abilities.allowModifyWorld ||
                    !player_holds_totemOfUndying_mainhand(player) ||
                    !player.getOffHandStack().isEmpty()) {
                return;
            }
            CarpetSettings.impendingFillSkipUpdates = true;
            boolean ret = BlockRotator.flip_block(state, world, player, hand, hit);
            CarpetSettings.impendingFillSkipUpdates = false;
            if (ret) {
                FlipCooldown.setCoolDown(player, world.getTime());
            }
            cir.setReturnValue(ret);
        }
    }

    @Inject(method = "flippinEligibility", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void postFlippinEligibility(Entity entity, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue() && PcaSettings.flippingTotemOfUndying && (entity instanceof PlayerEntity)) {
            PlayerEntity player = (PlayerEntity) entity;
            // 副手不为空，主手为图腾
            boolean ret = !player.getOffHandStack().isEmpty() && player_holds_totemOfUndying_mainhand(player);
            cir.setReturnValue(ret);
        }
    }

}