package syrena.foxo_origins.origin_layer;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.context.ContextValidator;
import io.github.eggohito.neo_apoli.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import io.github.eggohito.neo_apoli.util.manager.AbstractContentAndTagManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonFormat;
import org.slf4j.Logger;
import syrena.foxo_origins.event.DependencyManager;
import syrena.foxo_origins.event.OriginLayerPreparation;
import syrena.foxo_origins.event.OriginLayerReloadEvents;
import syrena.foxo_origins.network.packet.clientbound.ClientboundUpdateOriginLayersPacket;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ServerOriginLayerManager extends AbstractContentAndTagManager<ResourceLocation, OriginLayer> implements OriginLayerManager, IdentifiableResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Supplier<ImmutableSet<ResourceLocation>> DEPENDENCIES = Suppliers.memoize(() -> Util.make(ImmutableSet.builder(), DependencyManager.ORIGIN_LAYERS.invoker()::add).build());

    private final JsonFileToIdConverter contentLoader = new JsonFileToIdConverter(JsonFileToIdConverter.registry(FoxoOriginsRegistry.ORIGIN_LAYER).directory(), EnumSet.of(JsonFormat.JSON));
    private final TagLoader<OriginLayer> tagLoader = new TagLoader<>((id, required) -> this.getAsResult(id).result(), Registries.tagsDirPath(FoxoOriginsRegistry.ORIGIN_LAYER));

    private volatile Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = Map.of();
    private volatile DynamicOps<JsonElement> ops = JsonOps.INSTANCE;

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    private void send(ServerPlayer recipient) {
        ServerPlayNetworking.send(recipient, new ClientboundUpdateOriginLayersPacket(this.contents, this.tags));
    }
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {
        CompletableFuture<Map<ResourceLocation, JsonWithSource>> pendingOriginLayers = CompletableFuture
                .supplyAsync(() -> MiscUtil.collectJson(manager, contentLoader, ops, LOGGER::error), backgroundExecutor);
        CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> pendingTags = CompletableFuture
                .supplyAsync(() -> tagLoader.load(manager), backgroundExecutor);

        return pendingOriginLayers.thenCombine(pendingTags, Pair::of)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(pair -> this.apply(manager, Profiler.get(), pair.getFirst(), pair.getSecond()), gameExecutor);

    }

    private void apply(ResourceManager manager, ProfilerFiller profiler, Map<ResourceLocation, JsonWithSource> pendingOriginLayers, Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags) {

        OriginLayerReloadEvents.BEFORE.invoker().beforeReload(manager, profiler);
        LOGGER.info("Parsing origin layers from data packs...");

        ImmutableMap.Builder<ResourceLocation, OriginLayer> builder = ImmutableMap.builder();
        this.contents = ImmutableMap.of();

        pendingOriginLayers.forEach((id, jsonWithSource) -> {

            OriginLayerPreparation.EVENT.invoker().prepare(id, jsonWithSource, contentLoader.directory(), ops);

            ResourceLocationUtil.setCurrent(id);

            MiscUtil.handleResult(
                    OriginLayer.CODEC.parse(ops, jsonWithSource.json()),
                    layer -> builder.put(id,layer),
                    (thingy) -> true,
                    warning -> LOGGER.warn("Found warning(s) while parsing origin layer {} from data pack [{}]: {}", id.toString(), jsonWithSource.source(), warning),
                    error -> LOGGER.error("Error trying to parse origin layer {} from data pack [{}] (skipping): {}", id.toString(), jsonWithSource.source(), error)
            );

            ResourceLocationUtil.setCurrent(null);

        });

        this.contents = builder.build();
        this.pendingTags = pendingTags;

        OriginLayerReloadEvents.AFTER.invoker().afterReload(manager, profiler);
        LOGGER.info("Finished parsing origin layer from data packs. Parsed {} origin layer(s)", contents.size());

    }
    private ServerOriginLayerManager withOps(@NotNull HolderLookup.Provider provider) {
        this.ops = provider.createSerializationContext(JsonOps.INSTANCE);
        return this;
    }
    @Override
    public void init() {

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, this::withOps);
        DependencyManager.ORIGIN_LAYERS.register(ID, dependencies -> dependencies.add(OriginManager.ID));

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(OriginManager.ID, ID);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> {

            if (!joined) {
                this.send(player);
            }

        });

        ServerPlayConnectionEvents.INIT.addPhaseOrdering(OriginManager.ID, ID);
        ServerPlayConnectionEvents.INIT.register((handler, server) -> this.send(handler.player));

        OriginLayerPreparation.EVENT.register(OriginLayerManager.ID, (id, jsonWithSource, directoryPath, ops) -> {

            if (jsonWithSource.json() instanceof JsonObject jsonObject) {
                jsonObject.addProperty("id", id.toString());
            }

        });


        ReloadableServerResourcesEvents.TAGS_UPDATED.addPhaseOrdering(OriginManager.ID, ID);
        ReloadableServerResourcesEvents.TAGS_UPDATED.register(ID, this::finalize);

    }

    private void finalize(ReloadableServerResources resources) {

        int prevSize = contents.size();

        LOGGER.info("Validating {} origin layer(s)...", prevSize);
        LOGGER.info("{} origin layer(s) don't need to be validated but i don't wanna remove this line so. Origin layer manager contains {} origin layer(s)", prevSize, contents.size());

        LOGGER.info("Parsing origin layer tags from data packs...");
        this.tags = ImmutableMap.copyOf(tagLoader.build(pendingTags));

        LOGGER.info("Finished parsing origin layer tags from data packs. Origin layer manager contains {} origin layer tag(s)", tags.size());
        this.pendingTags = Map.of();

    }
}
