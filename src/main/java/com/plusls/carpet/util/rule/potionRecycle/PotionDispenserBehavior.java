package com.plusls.carpet.util.rule.potionRecycle;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.dispenser.MyFallibleItemDispenserBehavior;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

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
        BlockPos faceBlockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        ServerWorld world = pointer.getWorld();
        BlockState faceBlockState = world.getBlockState(faceBlockPos);
        if (faceBlockState.getBlock() instanceof AbstractCauldronBlock) {
            setSuccess(true);
            if (faceBlockState.getBlock() == Blocks.WATER_CAULDRON) {
                int level = faceBlockState.get(LeveledCauldronBlock.LEVEL);
                if (level == 3) {
                    return itemStack;
                } else {
                    world.setBlockState(faceBlockPos, faceBlockState.with(LeveledCauldronBlock.LEVEL, level + 1));
                    world.playSound(null, faceBlockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.FLUID_PLACE, faceBlockPos);
                    return new ItemStack(Items.GLASS_BOTTLE);

                }
            } else if (faceBlockState.getBlock() == Blocks.CAULDRON) {
                world.setBlockState(faceBlockPos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 1));
                world.playSound(null, faceBlockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(GameEvent.FLUID_PLACE, faceBlockPos);
                return new ItemStack(Items.GLASS_BOTTLE);
            }
        }
        return itemStack;
    }
}
