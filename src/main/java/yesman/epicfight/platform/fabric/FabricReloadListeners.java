package yesman.epicfight.platform.fabric;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import yesman.epicfight.EpicFight;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/// Registers Epic Fight's reload listeners the way NeoForge's `AddReloadListenerEvent` did: a fresh listener instance
/// is created on every reload, and listeners run in registration order.
public final class FabricReloadListeners {
    private ResourceLocation previous = null;
    private final PackType packType;

    public FabricReloadListeners(PackType packType) {
        this.packType = packType;
    }

    public FabricReloadListeners add(String path, Supplier<? extends PreparableReloadListener> factory) {
        ResourceLocation id = EpicFight.identifier(path);
        ResourceLocation dependency = this.previous;

        ResourceManagerHelper.get(this.packType).registerReloadListener(new Wrapper(id, dependency == null ? List.of() : List.of(dependency), factory));
        this.previous = id;

        return this;
    }

    private record Wrapper(ResourceLocation id, Collection<ResourceLocation> dependencies, Supplier<? extends PreparableReloadListener> factory) implements IdentifiableResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return this.id;
        }

        @Override
        public Collection<ResourceLocation> getFabricDependencies() {
            return this.dependencies;
        }

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return this.factory.get().reload(barrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
        }

        @Override
        public String getName() {
            return this.id.toString();
        }
    }
}
