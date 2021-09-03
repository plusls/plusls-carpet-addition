package com.plusls.carpet.mixin.rule.avoidAnvilTooExpensive;

import com.plusls.carpet.PcaSettings;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilContainer.class)
public class MixinAnvilScreenHandler {
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/container/Property;get()I", ordinal = 1))
    private int redirectGet(Property property) {
        if (PcaSettings.avoidAnvilTooExpensive) {
            return 0;
        } else {
            return property.get();
        }
    }
}
