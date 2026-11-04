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

public interface OriginLayerManager extends ContentAndTagManager<ResourceLocation, OriginLayerHolder> {
    ResourceLocation ID = FoxoOrigins.id("manager/origin_layer");
    Supplier<OriginLayerManager> DEFERRED_INSTANCE = KayaManagers.deferredPhysicalSideSpecific(OriginLayerManager.class, ServerOriginLayerManager::new);

    TagEntry.Lookup<OriginLayerHolder> TAG_LOOKUP = new TagEntry.Lookup<>() {

        @Override
        public @Nullable OriginLayerHolder element(ResourceLocation id, boolean required) {
            return getInstance().getAsResult(id).result().orElse(null);
        }

        @Override
        public @Nullable Collection<OriginLayerHolder> tag(ResourceLocation id) {
            return getInstance().getTagAsResult(id).result().orElse(null);
        }

        @Override
        public String toString() {
            return "OriginLayerHolder manager";
        }

    };

    @Override
    default DataResult<OriginLayerHolder> getAsResult(ResourceLocation key) {
        return this.getAsResult(key, k -> "Unknown " + k.toString());
    }


    @Override
    default DataResult<List<OriginLayerHolder>> getTagAsResult(ResourceLocation id) {
        return this.getTagAsResult(id, i -> "Unknown originLayer tag: \"" + i + "\"");
    }

    default DataResult<ResourceLocation> getKeyAsResult(OriginLayerHolder originLayer) {
        List<OriginLayerHolder> values = new ObjectArrayList<>(values().iterator());
        List<ResourceLocation> keys = new ObjectArrayList<>(keys().iterator());
        FoxoOrigins.LOGGER.info(String.valueOf(size()));
        Thread.dumpStack();
        for (int i=0;i<size();i++) {
            FoxoOrigins.LOGGER.info(values.get(i).layer().name().toString());
            FoxoOrigins.LOGGER.info(originLayer.layer().name().toString());
            if (values.get(i).equals(originLayer)) {
                FoxoOrigins.LOGGER.info("Equal");
                return DataResult.success(keys.get(i));
            }

        }
        FoxoOrigins.LOGGER.info("NOT equal...");
        return DataResult.error(() -> "Unregistered origin origin: " + originLayer.layer().name());
    }
    default ResourceLocation getKey(OriginLayerHolder originLayer) {
        return this.getKeyAsResult(originLayer).getOrThrow();
    }

    @ApiStatus.Internal
    void init();

    static OriginLayerManager getInstance() {
        return DEFERRED_INSTANCE.get();
    }

}
