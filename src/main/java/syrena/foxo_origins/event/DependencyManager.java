package syrena.foxo_origins.event;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public interface DependencyManager {

    Event<DependencyManager> ORIGINS = create();
    Event<DependencyManager> ORIGIN_LAYERS = create();
    void add(ImmutableSet.Builder<ResourceLocation> dependencies);

    private static Event<DependencyManager> create() {
        return EventFactory.createArrayBacked(
                DependencyManager.class,
                callbacks -> dependencies -> {

                    for (var callback : callbacks) {
                        callback.add(dependencies);
                    }

                }
        );
    }

}

