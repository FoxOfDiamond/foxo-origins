package syrena.foxo_origins.origin_layer;

import com.mojang.serialization.DataResult;
import io.github.eggohito.kaya.util.manager.ContentAndTagManager;
import io.github.eggohito.kaya.util.manager.KayaManagers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import syrena.foxo_origins.FoxoOrigins;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface OriginLayerManager extends ContentAndTagManager<ResourceLocation, OriginLayer> {
    ResourceLocation ID = FoxoOrigins.id("manager/origin_layer");
    Supplier<OriginLayerManager> DEFERRED_INSTANCE = KayaManagers.deferredPhysicalSideSpecific(OriginLayerManager.class, ServerOriginLayerManager::new);

    TagEntry.Lookup<OriginLayer> TAG_LOOKUP = new TagEntry.Lookup<>() {

        @Override
        public @Nullable OriginLayer element(ResourceLocation id, boolean required) {
            return getInstance().getAsResult(id).result().orElse(null);
        }

        @Override
        public @Nullable Collection<OriginLayer> tag(ResourceLocation id) {
            return getInstance().getTagAsResult(id).result().orElse(null);
        }

        @Override
        public String toString() {
            return "OriginLayer manager";
        }

    };

    @Override
    default DataResult<OriginLayer> getAsResult(ResourceLocation key) {
        return this.getAsResult(key, k -> "Unknown " + k.toString());
    }


    @Override
    default DataResult<List<OriginLayer>> getTagAsResult(ResourceLocation id) {
        return this.getTagAsResult(id, i -> "Unknown originLayer tag: \"" + i + "\"");
    }

    default DataResult<ResourceLocation> getKeyAsResult(OriginLayer originLayer) {
        List<OriginLayer> values = new ObjectArrayList<>(values().iterator());
        List<ResourceLocation> keys = new ObjectArrayList<>(keys().iterator());
        //FoxoOrigins.LOGGER.info(String.valueOf(size()));
        for (int i=0;i<size();i++) {
            //FoxoOrigins.LOGGER.info(values.get(i).name().toString());
            //FoxoOrigins.LOGGER.info(originLayer.name().toString());
            if (values.get(i) == originLayer) {
                return DataResult.success(keys.get(i));
            }

        }
        return DataResult.error(() -> "Unregistered origin layer: " + originLayer.name());
    }
    default ResourceLocation getKey(OriginLayer originLayer) {
        return this.getKeyAsResult(originLayer).getOrThrow();
    }

    @ApiStatus.Internal
    void init();

    static OriginLayerManager getInstance() {
        return DEFERRED_INSTANCE.get();
    }

}
