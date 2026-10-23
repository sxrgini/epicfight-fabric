package yesman.epicfight.platform.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import yesman.epicfight.platform.fabric.client.event.InputEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// Replacements for the commonly used static helpers of NeoForge's `ClientHooks`.
public final class FabricClientHooks {
    private FabricClientHooks() {}

    public static InputEvent.InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyMapping, InteractionHand hand) {
        return FabricGameEvents.BUS.post(new InputEvent.InteractionKeyMappingTriggered(button, keyMapping, hand));
    }

    public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean leftHand) {
        model.getTransforms().getTransform(context).apply(leftHand, poseStack);
        return model;
    }

    public static float getGuiFarPlane() {
        return 21000.0F;
    }

    /// Replaces NeoForge's `KeyMapping#getKey`
    public static com.mojang.blaze3d.platform.InputConstants.Key keyOf(net.minecraft.client.KeyMapping keyMapping) {
        return net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getBoundKeyOf(keyMapping);
    }
}
