package syrena.foxo_origins.origin;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.util.manager.ContentAndTagManager;
import io.github.eggohito.neo_apoli.util.manager.NeoApoliManagers;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import syrena.foxo_origins.FoxoOrigins;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OriginManager extends ContentAndTagManager<ResourceLocation, Holder<Origin>> {
    ResourceLocation ID = FoxoOrigins.id("manager/origin");
    Supplier<OriginManager> DEFERRED_INSTANCE = NeoApoliManagers.deferredPhysicalSideSpecific(OriginManager.class, ServerOriginManager::new);

    TagEntry.Lookup<Holder<Origin>> TAG_LOOKUP = new TagEntry.Lookup<>() {

        @Override
        public @Nullable Holder<Origin> element(ResourceLocation id, boolean required) {
            return getInstance().getAsResult(id).result().orElse(null);
        }

        @Override
        public @Nullable Collection<Holder<Origin>> tag(ResourceLocation id) {
            return getInstance().getTagAsResult(id).result().orElse(null);
        }

        @Override
        public String toString() {
            return "Origin manager";
        }

    };

    @Override
    default DataResult<Holder<Origin>> getAsResult(ResourceLocation key) {
        return this.getAsResult(key, k -> "Unknown " + k.toString());
    }

    @Override
    default DataResult<ResourceLocation> getKeyAsResult(Holder<Origin> value) {
        return this.getKeyAsResult(value, v -> "Unregistered origin holder: " + v);
    }

    @Override
    default DataResult<List<Holder<Origin>>> getTagAsResult(ResourceLocation id) {
        return this.getTagAsResult(id, i -> "Unknown origin tag: \"" + i + "\"");
    }

    default DataResult<ResourceLocation> getKeyAsResult(Origin origin) {

        for (Holder<Origin> candidate : values()) {

            if (candidate.value() == origin) {
                return DataResult.success(getKey(origin));
            }

        }

        return DataResult.error(() -> "Unregistered origin: " + origin);

    }

    default ResourceLocation getKey(Origin origin) {
        return this.getKeyAsResult(origin).getOrThrow();
    }

    @ApiStatus.Internal
    void init();

    static OriginManager getInstance() {
        return DEFERRED_INSTANCE.get();
    }

    static void handleSelf(Holder<Origin> holder, BiConsumer<ResourceLocation, Holder<Origin>> handler) {
        handler.accept(getInstance().getKey(holder.value()), holder);
    }
}
