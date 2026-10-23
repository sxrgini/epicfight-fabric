package yesman.epicfight.platform.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.SeparateTransformModels;

/// Emulates `neoforge:separate_transforms` item models.
@Mixin(ItemRenderer.class)
public abstract class MixinFabricItemRenderer {
    @Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"), cancellable = true)
    private void epicfight$swapModelByContext(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, BakedModel model, CallbackInfo ci) {
        String contextName = switch (context) {
            case GUI -> "gui";
            case FIXED -> "fixed";
            case GROUND -> "ground";
            default -> null;
        };

        if (contextName == null) {
            return;
        }

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (!"epicfight".equals(key.getNamespace()) || !SeparateTransformModels.NAMES.contains(key.getPath())) {
            return;
        }

        BakedModel variant = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(SeparateTransformModels.variant(key.getPath(), contextName), "fabric_resource"));

        if (variant == null || variant == model || variant == Minecraft.getInstance().getModelManager().getMissingModel()) {
            return;
        }

        ((ItemRenderer)(Object)this).render(stack, context, leftHand, poseStack, buffer, light, overlay, variant);
        ci.cancel();
    }
}
