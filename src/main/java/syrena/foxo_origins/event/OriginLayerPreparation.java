package syrena.foxo_origins.event;


import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public interface OriginLayerPreparation {
    Event<OriginLayerPreparation> EVENT = EventFactory.createArrayBacked(
            OriginLayerPreparation.class,
            callbacks -> (id, jsonWithSource, directoryPath, registryOps) -> {

                for (var callback : callbacks) {
                    callback.prepare(id, jsonWithSource, directoryPath, registryOps);
                }

            }
    );

    void prepare(ResourceLocation id, JsonWithSource jsonWithSource, String directoryPath, DynamicOps<JsonElement> ops);

}
