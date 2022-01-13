package com.plusls.carpet.mixin.rule.playerSit;

import com.mojang.authlib.GameProfile;
import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.playerSit.SitEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;
    private int sneakTimes = 0;
    private long lastSneakTime = 0;

    public MixinServerPlayerEntity(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        if (!PcaSettings.playerSit || (sneaking && this.isSneaking())) {
            super.setSneaking(sneaking);
            return;
        }

        if (sneaking) {
            long nowTime = System.currentTimeMillis();
            if (nowTime - lastSneakTime < 200 && sneakTimes == 0) {
                return;
            }
            super.setSneaking(true);
            if (this.onGround && nowTime - lastSneakTime < 200) {
                sneakTimes += 1;
                if (sneakTimes == 3) {
                    ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, this.x, this.y - 0.16, this.z);
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
            // 同步潜行状态到客户端
            // 如果不同步的话客户端会认为仍在潜行，从而碰撞箱的高度会计算错误
            if (sneakTimes == 0 && this.networkHandler != null) {
                this.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(this.getEntityId(), this.getDataTracker(), true));
            }
        }
    }

}
