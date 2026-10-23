package yesman.epicfight.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/// Replaces NeoForge's float-coordinate overloads of [GuiGraphics#drawString].
public final class GuiGraphicsExt {
    public static int drawString(GuiGraphics guiGraphics, Font font, String text, float x, float y, int color, boolean dropShadow) {
        int result = font.drawInBatch(text, x, y, color, dropShadow, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880, font.isBidirectional());
        guiGraphics.flushIfUnmanaged();

        return result;
    }

    private GuiGraphicsExt() {}
}
