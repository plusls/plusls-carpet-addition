package com.plusls.carpet.mixin.rule.villagerInventoryDrop;

import com.plusls.carpet.PcaSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;

@Mixin(VillagerEntity.class)
public class MixinVillagerInventoryDrop {

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    public void injected(DamageSource source, CallbackInfo ci) {
        if (PcaSettings.villagerDropInventory) {
            VillagerEntity ts = (VillagerEntity) (Object) this;
            for (int i = 0; i < ts.getInventory().size(); ++i) {
                ItemStack lv = ts.getInventory().getStack(i);
                while (!lv.isEmpty()) {
                    ts.dropStack(lv.split(3));
                }
            }
        }
    }
}
