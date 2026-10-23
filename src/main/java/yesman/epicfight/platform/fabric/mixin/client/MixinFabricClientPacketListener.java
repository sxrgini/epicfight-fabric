package yesman.epicfight.platform.fabric.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.event.impl.VanillaGeneralClientEventHooks;

/// ClientPlayerNetworkEvent.Clone
@Mixin(ClientPacketListener.class)
public abstract class MixinFabricClientPacketListener {
    @Unique
    private LocalPlayer epicfight$oldPlayer;

    @Inject(method = "handleRespawn(Lnet/minecraft/network/protocol/game/ClientboundRespawnPacket;)V", at = @At("HEAD"))
    private void epicfight$beforeRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        this.epicfight$oldPlayer = Minecraft.getInstance().player;
    }

    @Inject(method = "handleRespawn(Lnet/minecraft/network/protocol/game/ClientboundRespawnPacket;)V", at = @At("TAIL"))
    private void epicfight$afterRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        LocalPlayer oldPlayer = this.epicfight$oldPlayer;
        this.epicfight$oldPlayer = null;

        if (oldPlayer != null && Minecraft.getInstance().player != null && Minecraft.getInstance().player != oldPlayer) {
            VanillaGeneralClientEventHooks.onClonedInClient(oldPlayer, Minecraft.getInstance().player);
        }
    }
}
