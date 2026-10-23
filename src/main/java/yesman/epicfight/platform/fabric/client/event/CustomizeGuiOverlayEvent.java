package yesman.epicfight.platform.fabric.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.BossEvent;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class CustomizeGuiOverlayEvent extends FabricEvent {
    private final GuiGraphics guiGraphics;

    protected CustomizeGuiOverlayEvent(GuiGraphics guiGraphics) {
        this.guiGraphics = guiGraphics;
    }

    public GuiGraphics getGuiGraphics() { return this.guiGraphics; }

    public static class BossEventProgress extends CustomizeGuiOverlayEvent {
        private final BossEvent bossEvent;
        private final int x, y;

        public BossEventProgress(GuiGraphics guiGraphics, BossEvent bossEvent, int x, int y) {
            super(guiGraphics);
            this.bossEvent = bossEvent;
            this.x = x;
            this.y = y;
        }

        public BossEvent getBossEvent() { return this.bossEvent; }
        public int getX() { return this.x; }
        public int getY() { return this.y; }
    }
}
