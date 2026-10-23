package yesman.epicfight.platform.fabric.client.event;

import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class RenderHighlightEvent extends FabricEvent {
    public static class Block extends RenderHighlightEvent {}
}
