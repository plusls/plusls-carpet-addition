package com.plusls.carpet.mixin.rule.superLead;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MixinMobEntity {
    // 因为村民本身就可以交互，所以原版客户端可以直接用绳子拴住村民
    // 但是怪物本身是不可交互的，因此要想拴住怪物需要在客户端安装 PCA
    @Inject(method = "canBeLeashedBy", at = @At(value = "RETURN"), cancellable = true)
    private void postCanBeLeashedBy(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (PcaSettings.superLead) {
            cir.setReturnValue(!((MobEntity) (Object) this).isLeashed());
        }
    }

}
