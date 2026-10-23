package yesman.epicfight.platform.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.event.RenderHandEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// Fires `RenderHandEvent`.
@Mixin(ItemInHandRenderer.class)
public abstract class MixinFabricItemInHandRenderer {
    @Inject(
        method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void epicfight$renderHand(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (FabricGameEvents.BUS.hasListeners(RenderHandEvent.class)) {
            if (FabricGameEvents.BUS.post(new RenderHandEvent(hand, poseStack, buffer, combinedLight, partialTicks, stack)).isCanceled()) {
                ci.cancel();
            }
        }
    }
}
