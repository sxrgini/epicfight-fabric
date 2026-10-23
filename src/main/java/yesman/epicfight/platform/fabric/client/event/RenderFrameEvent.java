package yesman.epicfight.platform.fabric.client.event;

import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class RenderFrameEvent extends FabricEvent {
    public static class Pre extends RenderFrameEvent {}
    public static class Post extends RenderFrameEvent {}
}
