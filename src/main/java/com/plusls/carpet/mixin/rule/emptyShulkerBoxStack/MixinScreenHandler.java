package com.plusls.carpet.mixin.rule.emptyShulkerBoxStack;

import com.plusls.carpet.util.rule.emptyShulkerBoxStack.ShulkerBoxItemUtil;
import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Container.class)
public class MixinScreenHandler {
    @Redirect(method = "insertItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isStackable()Z", ordinal = 0))
    private boolean insertItemIsStackable(ItemStack itemStack) {
        return ShulkerBoxItemUtil.isStackable(itemStack);
    }

    @Redirect(method = "insertItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = -1))
    private int insertItemGetMaxCount0(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }

    @Redirect(method = "onSlotClick", // internalOnSlotClick
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = -1))
    private int removeStackGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }
}
