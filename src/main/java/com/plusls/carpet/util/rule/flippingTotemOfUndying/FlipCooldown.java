package com.plusls.carpet.util.rule.flippingTotemOfUndying;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class FlipCooldown {
    static private final Map<PlayerEntity, Long> cooldownMap = new HashMap<>();

    static public void init() {
        cooldownMap.clear();
    }

    static public long getCoolDown(PlayerEntity player) {
        return cooldownMap.getOrDefault(player, 0L);
    }

    static public void setCoolDown(PlayerEntity player, long cooldown) {
        cooldownMap.put(player, cooldown);
    }

    static public void removePlayer(PlayerEntity player) {
        cooldownMap.remove(player);
    }

}
