package com.plusls.carpet.util.rule.playerOperationLimiter;

public interface SafeServerPlayerEntity {
    int getInstaBreakCountPerTick();
    int getPlaceBlockCountPerTick();
    void addInstaBreakCountPerTick();
    void addPlaceBlockCountPerTick();
    boolean allowOperation();
}
