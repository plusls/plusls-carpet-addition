package com.plusls.carpet.mixin.rule.playersDropInventory;

import com.plusls.carpet.PcaSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ItemEntity;

import net.minecraft.entity.player.PlayerEntity;
@Mixin(PlayerEntity.class)
public class MixinPlayerDropInventory {

	@Inject(method ="dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
	public void injected(ItemStack stack, boolean throwRandomly, boolean retainOwnership,CallbackInfoReturnable<ItemEntity> cir) {
		if(throwRandomly && PcaSettings.normalizePlayerLootSpread){
            PlayerEntity ts=(PlayerEntity)(Object)this;
            ItemEntity lv = ts.dropStack(stack);
            lv.setPickupDelay(40);
            if (retainOwnership) {
                lv.setThrower(ts.getUuid());
            }
            cir.setReturnValue(lv);
		}
    }
}
