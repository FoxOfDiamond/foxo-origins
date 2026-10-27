package syrena.foxo_origins.event;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public final class OriginLayerReloadEvents {

    public static final Event<OriginLayerReloadEvents.Before> BEFORE = EventFactory.createArrayBacked(
            OriginLayerReloadEvents.Before.class,
            callbacks -> (manager, profiler) -> {

                for (var callback : callbacks) {
                    callback.beforeReload(manager, profiler);
                }

            }
    );

    public static final Event<OriginLayerReloadEvents.After> AFTER = EventFactory.createArrayBacked(
            OriginLayerReloadEvents.After.class,
            callbacks -> (manager, profiler) -> {

                for (var callback : callbacks) {
                    callback.afterReload(manager, profiler);
                }

            }
    );

    @FunctionalInterface
    public interface Before {
        void beforeReload(ResourceManager manager, ProfilerFiller profiler);
    }

    @FunctionalInterface
    public interface After {
        void afterReload(ResourceManager manager, ProfilerFiller profiler);
    }

}