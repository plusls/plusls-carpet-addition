package com.plusls.carpet.util.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

public abstract class MyFallibleItemDispenserBehavior extends MyDispenserBehavior {
    private boolean success = false;

    public MyFallibleItemDispenserBehavior(DispenserBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    @Override
    public final ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
        setSuccess(false);
        ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
        if (!isSuccess()) {
            return super.dispense(blockPointer, itemStack);
        }
        this.playSound(blockPointer);
        this.spawnParticles(blockPointer, blockPointer.getBlockState().get(DispenserBlock.FACING));
        return itemStack2;
    }

    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        Position position = DispenserBlock.getOutputLocation(pointer);
        ItemStack itemStack = stack.split(1);
        ItemDispenserBehavior.spawnItem(pointer.getWorld(), itemStack, 6, direction, position);
        return stack;
    }

    protected void spawnParticles(BlockPointer pointer, Direction side) {
        pointer.getWorld().syncWorldEvent(2000, pointer.getPos(), side.getId());
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    protected void playSound(BlockPointer pointer) {
        pointer.getWorld().syncWorldEvent(this.isSuccess() ? 1000 : 1001, pointer.getPos(), 0);
    }
}