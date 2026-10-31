package syrena.foxo_origins.origin;

import com.mojang.serialization.DataResult;
import io.github.eggohito.kaya.util.manager.ContentAndTagManager;
import io.github.eggohito.kaya.util.manager.KayaManagers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import syrena.foxo_origins.FoxoOrigins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OriginManager extends ContentAndTagManager<ResourceLocation, Origin> {
    ResourceLocation ID = FoxoOrigins.id("manager/origin");
    Supplier<OriginManager> DEFERRED_INSTANCE = KayaManagers.deferredPhysicalSideSpecific(OriginManager.class, ServerOriginManager::new);

    TagEntry.Lookup<Origin> TAG_LOOKUP = new TagEntry.Lookup<>() {

        @Override
        public @Nullable Origin element(ResourceLocation id, boolean required) {
            return getInstance().getAsResult(id).result().orElse(null);
        }

        @Override
        public @Nullable Collection<Origin> tag(ResourceLocation id) {
            return getInstance().getTagAsResult(id).result().orElse(null);
        }

        @Override
        public String toString() {
            return "Origin manager";
        }

    };

    @Override
    default DataResult<Origin> getAsResult(ResourceLocation key) {
        return this.getAsResult(key, k -> "Unknown " + k.toString());
    }


    @Override
    default DataResult<List<Origin>> getTagAsResult(ResourceLocation id) {
        return this.getTagAsResult(id, i -> "Unknown origin tag: \"" + i + "\"");
    }

    default DataResult<ResourceLocation> getKeyAsResult(Origin origin) {
        List<Origin> values = new ObjectArrayList<>(values().iterator());
        List<ResourceLocation> keys = new ObjectArrayList<>(keys().iterator());
        for (int i=0;i<size();i++) {

            if (values.get(i) == origin) {
                return DataResult.success(keys.get(i));
            }

        }

        return DataResult.error(() -> "Unregistered origin: " + origin.name());

    }

    default ResourceLocation getKey(Origin origin) {
        return this.getKeyAsResult(origin).getOrThrow();
    }

    @ApiStatus.Internal
    void init();

    static OriginManager getInstance() {
        return DEFERRED_INSTANCE.get();
    }

}
