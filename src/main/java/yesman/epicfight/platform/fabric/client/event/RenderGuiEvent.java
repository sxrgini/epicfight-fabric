package yesman.epicfight.platform.fabric.client.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class RenderGuiEvent extends FabricEvent {
    private final GuiGraphics guiGraphics;
    private final DeltaTracker deltaTracker;

    protected RenderGuiEvent(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        this.guiGraphics = guiGraphics;
        this.deltaTracker = deltaTracker;
    }

    public GuiGraphics getGuiGraphics() { return this.guiGraphics; }
    public DeltaTracker getPartialTick() { return this.deltaTracker; }

    public static class Pre extends RenderGuiEvent {
        public Pre(GuiGraphics guiGraphics, DeltaTracker deltaTracker) { super(guiGraphics, deltaTracker); }
    }
}
