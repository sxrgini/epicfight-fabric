package yesman.epicfight.registry.callbacks;

import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import yesman.epicfight.platform.fabric.registry.callback.BakeCallback;
import yesman.epicfight.platform.fabric.registry.callback.ClearCallback;
import yesman.epicfight.api.animation.SynchedAnimationVariableKey;
import yesman.epicfight.api.utils.datastructure.ClearableIdMapper;

public class SynchedAnimationVariableKeyCallbacks implements BakeCallback<SynchedAnimationVariableKey<?>>, ClearCallback<SynchedAnimationVariableKey<?>> {
	private static final SynchedAnimationVariableKeyCallbacks INSTANCE = new SynchedAnimationVariableKeyCallbacks();
	
	public static SynchedAnimationVariableKeyCallbacks getRegistryCallback() {
		return SynchedAnimationVariableKeyCallbacks.INSTANCE;
	}
	
	private static final ClearableIdMapper<SynchedAnimationVariableKey<?>> ID_MAPPER = new ClearableIdMapper<> ();
	
	@Override
	public void onClear(Registry<SynchedAnimationVariableKey<?>> registry, boolean full) {
		ID_MAPPER.clear();
	}
	
	@Override
	public void onBake(Registry<SynchedAnimationVariableKey<?>> registry) {
		// Sorted by name so ids stay identical between server and client regardless of registry sync remapping
		registry.keySet().stream().sorted().forEach(id -> ID_MAPPER.add(registry.get(id)));
	}
	
	public static IdMapper<SynchedAnimationVariableKey<?>> getIdMap() {
		return ID_MAPPER;
	}
}