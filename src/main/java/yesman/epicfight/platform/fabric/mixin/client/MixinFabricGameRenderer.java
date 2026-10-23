package yesman.epicfight.platform.fabric.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.event.RenderFrameEvent;
import yesman.epicfight.platform.fabric.client.event.ViewportEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// Fires `RenderFrameEvent` and `ViewportEvent.ComputeCameraAngles`.
@Mixin(GameRenderer.class)
public abstract class MixinFabricGameRenderer {
    @Shadow @Final
    private Camera mainCamera;

    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
    private void epicfight$renderFramePre(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        FabricGameEvents.BUS.post(new RenderFrameEvent.Pre());
    }

    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("RETURN"))
    private void epicfight$renderFramePost(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        FabricGameEvents.BUS.post(new RenderFrameEvent.Post());
    }

    @Inject(
        method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER)
    )
    private void epicfight$computeCameraAngles(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!FabricGameEvents.BUS.hasListeners(ViewportEvent.ComputeCameraAngles.class)) {
            return;
        }

        Camera camera = this.mainCamera;
        ViewportEvent.ComputeCameraAngles event = FabricGameEvents.BUS.post(new ViewportEvent.ComputeCameraAngles(camera, deltaTracker.getGameTimeDeltaPartialTick(true), camera.getYRot(), camera.getXRot(), 0.0F));

        camera.setRotation(event.getYaw(), event.getPitch());

        if (event.getRoll() != 0.0F) {
            camera.rotation().rotateZ(-event.getRoll() * ((float)Math.PI / 180.0F));
        }
    }
}
