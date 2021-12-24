package com.plusls.carpet.mixin.rule.playerSit;

import com.mojang.authlib.GameProfile;
import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.playerSit.SitEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    private int sneakTimes = 0;
    private long lastSneakTime = 0;

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        if (!PcaSettings.playerSit) {
            super.setSneaking(sneaking);
            return;
        }

        if (sneaking) {
            long nowTime = System.currentTimeMillis();
            if (nowTime - lastSneakTime < 200 && sneakTimes == 0) {
                return;
            }
            super.setSneaking(true);
            if (this.isOnGround() && nowTime - lastSneakTime < 200) {
                sneakTimes += 1;
                if (sneakTimes == 3) {
                    ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, this.getX(), this.getY() - 0.16, this.getZ());
                    ((SitEntity) armorStandEntity).setSitEntity(true);
                    world.spawnEntity(armorStandEntity);
                    this.setSneaking(false);
                    this.startRiding(armorStandEntity);
                    sneakTimes = 0;
                }
            } else {
                sneakTimes = 1;
            }
            lastSneakTime = nowTime;
        } else {
            super.setSneaking(false);
        }
    }

}
