package yesman.epicfight.platform.fabric.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.event.CustomizeGuiOverlayEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// Fires `CustomizeGuiOverlayEvent.BossEventProgress`.
@Mixin(BossHealthOverlay.class)
public abstract class MixinFabricBossHealthOverlay {
    @Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
    private void epicfight$bossEventProgress(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo ci) {
        if (FabricGameEvents.BUS.hasListeners(CustomizeGuiOverlayEvent.BossEventProgress.class)) {
            if (FabricGameEvents.BUS.post(new CustomizeGuiOverlayEvent.BossEventProgress(guiGraphics, bossEvent, x, y)).isCanceled()) {
                ci.cancel();
            }
        }
    }
}
