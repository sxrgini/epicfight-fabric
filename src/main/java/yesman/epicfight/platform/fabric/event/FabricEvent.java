package yesman.epicfight.platform.fabric.event;

/// Base for events that mirror the ones NeoForge fires. Cancelable events can be cancelled by listeners.
public abstract class FabricEvent {
    private boolean canceled;

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
