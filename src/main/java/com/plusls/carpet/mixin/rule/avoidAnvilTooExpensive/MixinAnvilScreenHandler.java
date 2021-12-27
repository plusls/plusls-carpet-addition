package com.plusls.carpet.mixin.rule.avoidAnvilTooExpensive;

import com.plusls.carpet.PcaSettings;
import net.minecraft.container.AnvilContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilContainer.class)
public abstract class MixinAnvilScreenHandler {
    @Shadow
    private String newItemName;

    @Final
    @Shadow
    private Inventory inventory;

    @ModifyVariable(method = "updateResult",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/container/Property;get()I", ordinal = 1)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0), ordinal = 1)
    private ItemStack setItemStack(ItemStack itemStack) {
        ItemStack inputStack = this.inventory.getInvStack(0);
        if (PcaSettings.avoidAnvilTooExpensive && itemStack.isEmpty() && !inputStack.isEmpty() &&
                (!this.inventory.getInvStack(1).isEmpty() ||
                        (StringUtils.isBlank(this.newItemName) && inputStack.hasCustomName()) ||
                        (!StringUtils.isBlank(this.newItemName) && !this.newItemName.equals(inputStack.getName().getString())))) {
            return inputStack.copy();
        } else {
            return itemStack;
        }
    }
}
