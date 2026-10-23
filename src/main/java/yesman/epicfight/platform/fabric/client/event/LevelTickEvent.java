package yesman.epicfight.platform.fabric.client.event;

import net.minecraft.world.level.Level;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class LevelTickEvent extends FabricEvent {
    private final Level level;

    protected LevelTickEvent(Level level) { this.level = level; }

    public Level getLevel() { return this.level; }

    public static class Post extends LevelTickEvent {
        public Post(Level level) { super(level); }
    }
}
