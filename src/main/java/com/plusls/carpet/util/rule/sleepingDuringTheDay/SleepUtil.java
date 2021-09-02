package com.plusls.carpet.util.rule.sleepingDuringTheDay;

import com.plusls.carpet.PcaSettings;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.util.ActionResult;

public class SleepUtil {
    public static void init() {
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            if (PcaSettings.sleepingDuringTheDay) {
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        });
    }
}
