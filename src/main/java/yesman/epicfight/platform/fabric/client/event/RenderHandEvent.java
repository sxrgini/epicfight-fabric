package yesman.epicfight.platform.fabric.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public class RenderHandEvent extends FabricEvent {
    private final InteractionHand hand;
    private final PoseStack poseStack;
    private final MultiBufferSource buffer;
    private final int packedLight;
    private final float partialTick;
    private final ItemStack stack;

    public RenderHandEvent(InteractionHand hand, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick, ItemStack stack) {
        this.hand = hand;
        this.poseStack = poseStack;
        this.buffer = buffer;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
        this.stack = stack;
    }

    public InteractionHand getHand() { return this.hand; }
    public PoseStack getPoseStack() { return this.poseStack; }
    public MultiBufferSource getMultiBufferSource() { return this.buffer; }
    public int getPackedLight() { return this.packedLight; }
    public float getPartialTick() { return this.partialTick; }
    public ItemStack getItemStack() { return this.stack; }
}
