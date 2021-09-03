package com.plusls.carpet.util.rule.powerfulBoneMeal;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Grow {
    static public boolean grow(ItemStack itemStack, World world, BlockPos pos, Block block) {
        if (block instanceof SugarCaneBlock) {
            return growSugarCaneBlock(itemStack, world, pos);
        } else if (block instanceof ChorusFlowerBlock) {
            // TODO
            return false;
        } else {
            return false;
        }
    }

    static private boolean growSugarCaneBlock(ItemStack itemStack, World world, BlockPos pos) {
        BlockPos downPos = pos.down();
        BlockPos upPos = pos.up();
        int height = 1;
        // 计算上层空气坐标
        while (!world.isAir(upPos)) {
            if (world.getBlockState(upPos).getBlock() == Blocks.SUGAR_CANE) {
                upPos = upPos.up();
                height++;
            } else {
                return false;
            }
        }

        // 计算底部坐标
        while (world.getBlockState(downPos).getBlock() == Blocks.SUGAR_CANE) {
            downPos = downPos.down();
            height++;
        }

        // 甘蔗最多长 3 格
        if (height < 3) {
            BlockPos sugarCanePos = upPos.down();
            BlockState blockState = world.getBlockState(sugarCanePos);

            int age = blockState.get(SugarCaneBlock.AGE);
            if (age == 15) {
                world.setBlockState(upPos, Blocks.SUGAR_CANE.getDefaultState());
                world.setBlockState(sugarCanePos, blockState.with(SugarCaneBlock.AGE, 0), 4);
            } else {
                age = Math.min(15, age + world.random.nextInt(16));
                world.setBlockState(sugarCanePos, blockState.with(SugarCaneBlock.AGE, age), 4);
            }
            itemStack.decrement(1);
            return true;
        } else {
            return false;
        }
    }
}