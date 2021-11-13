package com.plusls.carpet.util.rule.dispenserFixIronGolem;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.dispenser.MyFallibleItemDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class IronIngotDispenserBehavior extends MyFallibleItemDispenserBehavior {

    public IronIngotDispenserBehavior(DispenserBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    public static void init() {
        DispenserBlock.registerBehavior(Items.IRON_INGOT,
                new IronIngotDispenserBehavior(DispenserBlock.BEHAVIORS.get(Items.IRON_INGOT)));
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack itemStack) {
        if (!PcaSettings.dispenserFixIronGolem) {
            return itemStack;
        }
        BlockPos faceBlockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

        List<IronGolemEntity> ironGolemEntityList = pointer.getWorld().getEntities(IronGolemEntity.class,
                new Box(faceBlockPos), LivingEntity::isAlive);

        for (IronGolemEntity ironGolemEntity : ironGolemEntityList) {
            float oldHealth = ironGolemEntity.getHealth();
            ironGolemEntity.heal(25.0F);
            if (ironGolemEntity.getHealth() == oldHealth) {
                continue;
            }
            float g = 1.0F + (ironGolemEntity.getRandom().nextFloat() - ironGolemEntity.getRandom().nextFloat()) * 0.2F;
            itemStack.decrement(1);
            setSuccess(true);
            return itemStack;
        }
        return itemStack;
    }
}