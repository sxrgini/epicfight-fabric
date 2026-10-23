package yesman.epicfight.platform.fabric.client.event;

import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class ClientTickEvent extends FabricEvent {
    public static class Pre extends ClientTickEvent {}
    public static class Post extends ClientTickEvent {}
}
