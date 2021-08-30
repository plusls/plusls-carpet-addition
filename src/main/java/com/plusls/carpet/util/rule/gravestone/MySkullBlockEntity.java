package com.plusls.carpet.util.rule.gravestone;

import org.jetbrains.annotations.Nullable;

public interface MySkullBlockEntity {
    @Nullable
    DeathInfo getDeathInfo();

    void setDeathInfo(DeathInfo deathInfo);

}
