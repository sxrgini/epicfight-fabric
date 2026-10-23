package yesman.epicfight.platform.fabric.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.event.RenderGuiEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// RenderGuiEvent.Pre
@Mixin(Gui.class)
public abstract class MixinFabricGui {
    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", at = @At("HEAD"))
    private void epicfight$renderPre(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        FabricGameEvents.BUS.post(new RenderGuiEvent.Pre(guiGraphics, deltaTracker));
    }
}
