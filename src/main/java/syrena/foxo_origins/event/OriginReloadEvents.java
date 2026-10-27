package syrena.foxo_origins.event;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public final class OriginReloadEvents {

    public static final Event<OriginReloadEvents.Before> BEFORE = EventFactory.createArrayBacked(
            OriginReloadEvents.Before.class,
            callbacks -> (manager, profiler) -> {

                for (var callback : callbacks) {
                    callback.beforeReload(manager, profiler);
                }

            }
    );

    public static final Event<OriginReloadEvents.After> AFTER = EventFactory.createArrayBacked(
            OriginReloadEvents.After.class,
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