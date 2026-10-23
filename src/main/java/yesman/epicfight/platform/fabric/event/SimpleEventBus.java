package yesman.epicfight.platform.fabric.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/// A tiny event bus that replaces NeoForge's `IEventBus` for the events Epic Fight listens to on Fabric.
public final class SimpleEventBus {
    private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    public <T> void addListener(Class<T> eventType, Consumer<? super T> listener) {
        this.listeners.computeIfAbsent(eventType, key -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> T post(T event) {
        List<Consumer<?>> list = this.listeners.get(event.getClass());

        if (list != null) {
            for (Consumer<?> listener : list) {
                ((Consumer<T>)listener).accept(event);
            }
        }

        return event;
    }

    public boolean hasListeners(Class<?> eventType) {
        return this.listeners.containsKey(eventType);
    }
}
