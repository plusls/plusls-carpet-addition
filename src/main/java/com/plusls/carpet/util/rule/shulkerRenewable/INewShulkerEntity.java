package com.plusls.carpet.util.rule.shulkerRenewable;

import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

public interface INewShulkerEntity {
    public void setColor(DyeColor color);

    @Nullable
    public DyeColor getColor();
}
