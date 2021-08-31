package com.plusls.carpet.mixin.rule.villagersAttractedByEmeraldBlock;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity extends MerchantEntity {
    public MixinVillagerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    boolean villagersAttractedByEmeraldBlock;

    private TemptGoal villagersAttractedByEmeraldBlockGoal;

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;Lnet/minecraft/village/VillagerType;)V", at = @At(value = "RETURN"))
    private void init(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type, CallbackInfo ci) {
        if (this.world.isClient()) {
            return;
        }
        villagersAttractedByEmeraldBlockGoal = new TemptGoal(this, 1.0D, Ingredient.ofItems(Items.EMERALD_BLOCK), false);
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void checkVillagersAttractedByEmeraldBlock(CallbackInfo ci) {
        if (this.world.isClient()) {
            return;
        }
        if (!villagersAttractedByEmeraldBlock && PcaSettings.villagersAttractedByEmeraldBlock) {
            if (!villagersAttractedByEmeraldBlockGoal.canStart()) {
                villagersAttractedByEmeraldBlockGoal = new TemptGoal(this, 1.0D, Ingredient.ofItems(Items.EMERALD_BLOCK), false);
            }
            this.goalSelector.add(0, villagersAttractedByEmeraldBlockGoal);
            villagersAttractedByEmeraldBlock = PcaSettings.villagersAttractedByEmeraldBlock;
        } else if (villagersAttractedByEmeraldBlock && !PcaSettings.villagersAttractedByEmeraldBlock) {
            this.goalSelector.remove(villagersAttractedByEmeraldBlockGoal);
            villagersAttractedByEmeraldBlock = PcaSettings.villagersAttractedByEmeraldBlock;
        }
    }
}