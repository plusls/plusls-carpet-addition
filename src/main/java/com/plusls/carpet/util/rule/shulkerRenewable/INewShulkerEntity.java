package com.plusls.carpet.util.rule.shulkerRenewable;

import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

public interface INewShulkerEntity {
    @Nullable
    public DyeColor getColor();

    public void setColor(DyeColor color);
}
