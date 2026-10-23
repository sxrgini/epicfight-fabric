package yesman.epicfight.platform.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.ArrayList;
import java.util.List;

/// Captures item entities spawned by an entity while it drops its death loot, so that
/// they can be filtered before they are added to the level (replaces NeoForge's `LivingDropsEvent`).
public final class DropCapture {
    private record Frame(Entity entity, List<ItemEntity> drops) {}

    private static final ThreadLocal<List<Frame>> FRAMES = ThreadLocal.withInitial(ArrayList::new);

    public static List<ItemEntity> begin(Entity entity) {
        List<ItemEntity> drops = new ArrayList<>();
        FRAMES.get().add(new Frame(entity, drops));
        return drops;
    }

    public static void end(Entity entity) {
        List<Frame> frames = FRAMES.get();

        for (int i = frames.size() - 1; i >= 0; i--) {
            if (frames.get(i).entity() == entity) {
                frames.remove(i);
                return;
            }
        }
    }

    /// @return whether the item entity has been captured
    public static boolean capture(Entity spawner, ItemEntity itemEntity) {
        List<Frame> frames = FRAMES.get();

        for (int i = frames.size() - 1; i >= 0; i--) {
            if (frames.get(i).entity() == spawner) {
                frames.get(i).drops().add(itemEntity);
                return true;
            }
        }

        return false;
    }

    private DropCapture() {}
}
