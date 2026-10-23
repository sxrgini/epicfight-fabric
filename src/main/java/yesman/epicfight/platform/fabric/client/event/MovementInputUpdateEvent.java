package yesman.epicfight.platform.fabric.client.event;

import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public class MovementInputUpdateEvent extends FabricEvent {
    private final Input input;
    private final Player player;

    public MovementInputUpdateEvent(Player player, Input input) {
        this.player = player;
        this.input = input;
    }

    public Input getInput() { return this.input; }
    public Player getEntity() { return this.player; }
}
