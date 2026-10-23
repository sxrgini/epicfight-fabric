package yesman.epicfight.main;

import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.platform.fabric.registry.DeferredHolder;
import yesman.epicfight.registry.entries.EpicFightCreativeTabs;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Param skillBookCreativeTab : decides which creative tab will display the skills that belong to the mod {@link EpicFightCreativeTabs}}
 *
 * On Fabric, addons register their extension through {@link #register(String, EpicFightExtensions)} using their mod id.
 */
public record EpicFightExtensions(DeferredHolder<CreativeModeTab, CreativeModeTab> skillBookCreativeTab) {
	private static final Map<String, EpicFightExtensions> EXTENSIONS = new ConcurrentHashMap<>();
	
	public static void register(String modid, EpicFightExtensions extension) {
		EXTENSIONS.put(modid, extension);
	}
	
	public static Optional<EpicFightExtensions> get(String modid) {
		return Optional.ofNullable(EXTENSIONS.get(modid));
	}
}
