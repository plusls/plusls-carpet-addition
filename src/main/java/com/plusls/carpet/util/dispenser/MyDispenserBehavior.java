package com.plusls.carpet.util.dispenser;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public class MyDispenserBehavior implements DispenserBehavior {

    private final DispenserBehavior oldDispenserBehavior;

    public MyDispenserBehavior(DispenserBehavior oldDispenserBehavior) {
        this.oldDispenserBehavior = oldDispenserBehavior;
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        return oldDispenserBehavior.dispense(pointer, stack);
    }
}
