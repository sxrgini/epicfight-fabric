package yesman.epicfight.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import yesman.epicfight.platform.fabric.FabricHooks;
import yesman.epicfight.generated.LangKeys;
import yesman.epicfight.main.EpicFightMod;

public class VersionNotifier {
    private final Minecraft minecraft;
    private final boolean visible;
    private int count = 0;

    public VersionNotifier(Minecraft minecraft) {
        this.minecraft = minecraft;
        // Upstream shows the "testing version" popup for four-part versions. The Fabric port uses a fourth number
        // as its build counter, so the popup is disabled.
        this.visible = false;
    }

    public void init() {
        this.count = 0;
    }

    public void render(GuiGraphics guiGraphics, boolean inWorld) {
        if (!this.visible) {
            return;
        }

        if (this.count > 600 && inWorld) {
            return;
        }

        this.count++;
        Window sr = this.minecraft.getWindow();
        int width = sr.getGuiScaledWidth();

        if (inWorld) {
            String l1 = Component.translatable(LangKeys.GUI_MESSAGE_VERSION_NOTIFIER_TEST_VERSION_WARNING_LINE1).getString();
            String l2 = Component.translatable(LangKeys.GUI_MESSAGE_VERSION_NOTIFIER_TEST_VERSION_WARNING_LINE2, FabricHooks.modVersion(EpicFightMod.MODID)).getString();

            guiGraphics.drawString(this.minecraft.font, l1, (width - this.minecraft.font.width(l1) - 2), 8, 16777215);
            guiGraphics.drawString(this.minecraft.font, l2, (width - this.minecraft.font.width(l2) - 2), 20, 16777215);
        } else {
            String l1 = Component.translatable(LangKeys.GUI_MESSAGE_VERSION_NOTIFIER, FabricHooks.modVersion(EpicFightMod.MODID)).getString();
            guiGraphics.drawString(this.minecraft.font, l1, (width - this.minecraft.font.width(l1) - 2), 8, 16777215);
        }
    }
}
