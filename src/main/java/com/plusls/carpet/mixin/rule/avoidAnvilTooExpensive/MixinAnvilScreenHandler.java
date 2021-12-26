package com.plusls.carpet.mixin.rule.avoidAnvilTooExpensive;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilScreenHandler.class)
public abstract class MixinAnvilScreenHandler extends ForgingScreenHandler {
    @Shadow
    private String newItemName;

    public MixinAnvilScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @ModifyVariable(method = "updateResult",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I", ordinal = 1)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0), ordinal = 1)
    private ItemStack setItemStack(ItemStack itemStack) {
        ItemStack inputStack = this.input.getStack(0);
        if (PcaSettings.avoidAnvilTooExpensive && itemStack.isEmpty() && !inputStack.isEmpty() &&
                (this.input.getStack(1) != ItemStack.EMPTY ||
                        (StringUtils.isBlank(this.newItemName) && inputStack.hasCustomName()) ||
                        (!StringUtils.isBlank(this.newItemName) && !this.newItemName.equals(inputStack.getName().getString())))) {
            return inputStack.copy();
        } else {
            return itemStack;
        }
    }
}
