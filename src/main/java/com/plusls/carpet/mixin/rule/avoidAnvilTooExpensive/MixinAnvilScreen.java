package com.plusls.carpet.mixin.rule.avoidAnvilTooExpensive;

import com.plusls.carpet.PcaSettings;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen {
    @Redirect(method = "drawForeground", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z"))
    private boolean aa(PlayerAbilities playerAbilities) {
        boolean ret = playerAbilities.creativeMode;
        if (!ret && PcaSettings.avoidAnvilTooExpensive) {
            ret = true;
        }
        return ret;
    }
}
