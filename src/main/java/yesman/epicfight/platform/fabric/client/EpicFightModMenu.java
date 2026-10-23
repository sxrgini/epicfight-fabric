package yesman.epicfight.platform.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import yesman.epicfight.client.gui.screen.config.EpicFightSettingScreen;

/// Opens Epic Fight's own settings screen from Mod Menu.
public class EpicFightModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new EpicFightSettingScreen(null, parent);
    }
}
