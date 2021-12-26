package com.plusls.carpet.mixin.rule.autoTrade;

import com.plusls.carpet.util.rule.autoTrade.MyVillagerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity extends MerchantEntity implements MyVillagerEntity {
    @Shadow
    private int experience;
    @Shadow
    private int levelUpTimer;
    @Shadow
    private boolean levelingUp;

    @Shadow
    protected abstract boolean canLevelUp();


    public MixinVillagerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    public void tradeWithoutPlayer(TradeOffer offer) {
        this.experience += offer.getMerchantExperience();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levelingUp = true;
        }
    }

}
