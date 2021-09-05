package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem extends Item {
    public MixinBlockItem(Settings settings) {
        super(settings);
    }

    @Shadow
    public abstract ItemPlacementContext getPlacementContext(ItemPlacementContext context);

    @Shadow
    protected abstract BlockState getPlacementState(ItemPlacementContext context);

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "HEAD"), cancellable = true)
    private void checkOperationCountPerTick(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!PcaSettings.playerOperationLimiter || context.getWorld().isClient()) {
            return;
        }

        if (context.canPlace()) {
            ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
            SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity) context.getPlayer();
            if (safeServerPlayerEntity != null && itemPlacementContext != null && this.getPlacementState(itemPlacementContext) != null) {
                safeServerPlayerEntity.addPlaceBlockCountPerTick();
                if (!safeServerPlayerEntity.allowOperation()) {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            }

        }

    }
}
