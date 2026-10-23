package yesman.epicfight.platform.fabric.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public class RenderLevelStageEvent extends FabricEvent {
    public enum Stage { AFTER_TRIPWIRE_BLOCKS }

    private final Stage stage;
    private final PoseStack poseStack;
    private final Camera camera;

    public RenderLevelStageEvent(Stage stage, PoseStack poseStack, Camera camera) {
        this.stage = stage;
        this.poseStack = poseStack;
        this.camera = camera;
    }

    public Stage getStage() { return this.stage; }
    public PoseStack getPoseStack() { return this.poseStack; }
    public Camera getCamera() { return this.camera; }
}
