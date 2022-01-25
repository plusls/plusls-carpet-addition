package com.plusls.carpet.mixin.rule.trackItemPickupByPlayer;

import carpet.CarpetSettings;
import carpet.utils.Translations;
import com.plusls.carpet.PcaMod;
import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    @Shadow
    public abstract void tick();

    private boolean pickuped = false;
    private int trackItemPickupByPlayerCooldown = 0;
    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract void setStack(ItemStack stack);

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void prevTick(CallbackInfo ci) {
        if (PcaSettings.trackItemPickupByPlayer && pickuped) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"), cancellable = true)
    private void checkPickup(PlayerEntity player, CallbackInfo ci) {
        if (PcaSettings.trackItemPickupByPlayer && PcaMod.server != null) {
            Text text = new LiteralText(String.format(Translations.tr("pca.message.pickup"), player.getName().asString(),
                    this.getX(), this.getY(), this.getZ(),
                    this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ()));
            if (trackItemPickupByPlayerCooldown == 0) {
                PcaMod.server.getPlayerManager().broadcast(text, MessageType.CHAT, Util.NIL_UUID);
            }
            trackItemPickupByPlayerCooldown = (trackItemPickupByPlayerCooldown + 1) % 10;
            pickuped = true;
            this.setStack(new ItemStack(Items.BARRIER));
            this.setNoGravity(true);
            this.noClip = true;
            this.setVelocity(0, 0, 0);
            ci.cancel();
        }
    }

}
