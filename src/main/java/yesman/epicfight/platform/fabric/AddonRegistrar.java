package yesman.epicfight.platform.fabric;

import yesman.epicfight.platform.fabric.registry.DeferredRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Collects the [DeferredRegister]s of addon mods (see the "epicfight-addon" entrypoint) so that they are written to the
/// registries together with Epic Fight's own, in the same registry order NeoForge would use.
public final class AddonRegistrar {
    private static final List<DeferredRegister<?>> QUEUED = new ArrayList<>();

    private AddonRegistrar() {
    }

    public static void queue(DeferredRegister<?>... registers) {
        QUEUED.addAll(Arrays.asList(registers));
    }

    public static void queue(List<? extends DeferredRegister<?>> registers) {
        QUEUED.addAll(registers);
    }

    private static final List<java.util.function.Consumer<yesman.epicfight.platform.fabric.event.EntityAttributeCreationEvent>> ATTRIBUTES = new ArrayList<>();
    private static final List<Runnable> POST = new ArrayList<>();

    /// Called once every queued registry is written and baked; default entity attributes may be registered here.
    public static void onAttributes(java.util.function.Consumer<yesman.epicfight.platform.fabric.event.EntityAttributeCreationEvent> listener) {
        ATTRIBUTES.add(listener);
    }

    /// Runs once every queued registry is written and baked (NeoForge's common setup equivalent).
    public static void afterRegistration(Runnable task) {
        POST.add(task);
    }

    static void fireAttributes(yesman.epicfight.platform.fabric.event.EntityAttributeCreationEvent event) {
        ATTRIBUTES.forEach(l -> l.accept(event));
    }

    static void firePost() {
        POST.forEach(Runnable::run);
    }

    public static List<DeferredRegister<?>> drain() {
        List<DeferredRegister<?>> copy = new ArrayList<>(QUEUED);
        QUEUED.clear();
        return copy;
    }
}
