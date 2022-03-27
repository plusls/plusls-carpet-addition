package com.plusls.carpet.util.rule.potionRecycle;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.dispenser.MyFallibleItemDispenserBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PotionDispenserBehavior extends MyFallibleItemDispenserBehavior {

    public PotionDispenserBehavior(DispenserBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    public static void init() {
        DispenserBlock.registerBehavior(Items.POTION,
                new PotionDispenserBehavior(DispenserBlock.BEHAVIORS.get(Items.POTION)));
        DispenserBlock.registerBehavior(Items.SPLASH_POTION,
                new PotionDispenserBehavior(DispenserBlock.BEHAVIORS.get(Items.SPLASH_POTION)));
        DispenserBlock.registerBehavior(Items.LINGERING_POTION,
                new PotionDispenserBehavior(DispenserBlock.BEHAVIORS.get(Items.LINGERING_POTION)));
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack itemStack) {
        if (!PcaSettings.potionRecycle) {
            return itemStack;
        }
        BlockPos faceBlockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        World world = pointer.getWorld();
        BlockState faceBlockState = world.getBlockState(faceBlockPos);
        if (faceBlockState.getBlock() instanceof CauldronBlock) {
            setSuccess(true);
            int level = faceBlockState.get(CauldronBlock.LEVEL);
            if (level == 3) {
                return itemStack;
            } else {
                world.setBlockState(faceBlockPos, faceBlockState.with(CauldronBlock.LEVEL, level + 1));
                world.playSound(null, faceBlockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return new ItemStack(Items.GLASS_BOTTLE);

            }
        }
        return itemStack;
    }
}
