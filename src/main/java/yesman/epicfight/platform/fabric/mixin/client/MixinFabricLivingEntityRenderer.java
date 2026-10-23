package yesman.epicfight.platform.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.event.RenderLivingEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// Fires the NeoForge-equivalent `RenderLivingEvent.Pre`.
@Mixin(LivingEntityRenderer.class)
public abstract class MixinFabricLivingEntityRenderer {
    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void epicfight$renderLivingPre(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (FabricGameEvents.BUS.hasListeners(RenderLivingEvent.Pre.class)) {
            RenderLivingEvent.Pre<?, ?> event = FabricGameEvents.BUS.post(new RenderLivingEvent.Pre<>(entity, (LivingEntityRenderer<?, ?>)(Object)this, partialTicks, poseStack, buffer, packedLight));

            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }
}
