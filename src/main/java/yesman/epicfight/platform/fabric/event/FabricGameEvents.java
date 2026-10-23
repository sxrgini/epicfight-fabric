package yesman.epicfight.platform.fabric.event;

/// Global bus that Fabric mixins and callbacks post NeoForge-equivalent game events to.
public final class FabricGameEvents {
    private FabricGameEvents() {}

    public static final SimpleEventBus BUS = new SimpleEventBus();
}
