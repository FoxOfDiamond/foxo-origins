package syrena.foxo_origins.attachment.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import io.github.eggohito.kaya.power.entity.MutablePowers;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.HashMap;
import java.util.Map;

public class FoxoOriginsAttachment {
    /*public static final Codec<Map<OriginLayer,Origin>> CODEC = new Codec<>() {
        final Map<OriginLayer,ResourceLocation> cachedLayerId = new HashMap<>();
        final Map<Origin,ResourceLocation> cachedOriginId = new HashMap<>();
        @Override
        public <T> DataResult<Pair<Map<OriginLayer, Origin>, T>> decode(DynamicOps<T> ops, T input) {
            return Codec.unboundedMap(ResourceLocation.CODEC,ResourceLocation.CODEC).parse(ops,input).flatMap(idMap -> {
                Map<OriginLayer,Origin> decoded = new HashMap<>();

                idMap.forEach((layerId, originId) -> {
                    for (int i = 0; i < cachedLayerId.size(); i++) {
                        if (cachedOriginId.values().toArray()[i].equals(layerId)){
                            for (int j = 0; j < cachedOriginId.size(); j++) {
                                if (cachedOriginId.values().toArray()[i].equals(originId)){
                                    decoded.put((OriginLayer)cachedLayerId.keySet().toArray()[i],(Origin)cachedOriginId.keySet().toArray()[j]);
                                }
                            }
                        }
                    }
                    Origin origin = OriginManager.getInstance().get(originId);
                    OriginLayer layer = OriginLayerManager.getInstance().get(layerId);
                    cachedOriginId.put(origin,originId);
                    cachedLayerId.put(layer,layerId);
                    decoded.put(layer,origin);
                });
                return DataResult.success(ImmutableMap.copyOf(decoded));
            }).map(decoded -> Pair.of(decoded,input));
        }

        @Override
        public <T> DataResult<T> encode(Map<OriginLayer, Origin> input, DynamicOps<T> ops, T prefix) {
            DataResult<Unit> identity = DataResult.success(Unit.INSTANCE);

            Map<ResourceLocation,ResourceLocation> encoded = new HashMap<>();
            input.forEach((layer, origin) -> {
                DataResult<ResourceLocation> layerId = OriginLayerManager.getInstance().getKeyAsResult(layer);
                DataResult<ResourceLocation> originId = OriginManager.getInstance().getKeyAsResult(origin);
                if (layerId.isSuccess()) cachedLayerId.put(layer,layerId.getOrThrow());
                if (originId.isSuccess()) cachedOriginId.put(origin,originId.getOrThrow());
                encoded.put(cachedLayerId.get(layer),cachedOriginId.get(origin));
            });
            return identity
                    .map(_ -> encoded)
                    .setPartial(encoded)
                    .map(Function.identity())
                    .flatMap(encodedMap ->
                            Codec.unboundedMap(ResourceLocation.CODEC,ResourceLocation.CODEC).encode(encodedMap,ops,prefix));
        }
    };*/
    public static final Codec<Map<OriginLayer,Origin>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC.flatXmap(OriginLayerManager.getInstance()::getAsResult,OriginLayerManager.getInstance()::getKeyAsResult),ResourceLocation.CODEC.flatXmap(OriginManager.getInstance()::getAsResult,OriginManager.getInstance()::getKeyAsResult));
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<OriginLayer, Origin>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new, // Constructs a map with the specified capacity
                    ResourceLocation.STREAM_CODEC.map(OriginLayerManager.getInstance()::get,OriginLayerManager.getInstance()::getKey),
                    ResourceLocation.STREAM_CODEC.map(OriginManager.getInstance()::get,OriginManager.getInstance()::getKey),
                    256 // The map can only have up to 256 elements
            );
    public static void refreshOrigins(Entity entity, Map<OriginLayer,Origin> origins){
        for (OriginLayer layer: OriginLayerManager.getInstance().values()){
            if (layer.defaultOrigin().isPresent() && !origins.containsKey(layer)){
                HashMap<OriginLayer,Origin> mutableAttachment = new HashMap<>(origins);

                mutableAttachment.put(layer,layer.defaultOrigin().get());
                entity.setAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT, ImmutableMap.copyOf(mutableAttachment));
                refreshOrigins(entity, entity.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT));
                return;
            }
        }
        MutablePowers mutable = MutablePowers.create(entity);
        origins.forEach((layer,origin) -> {
            origin.powers().elements().forEach(power -> {
                mutable.grant(power.id(),powerSource(layer));
            });
        });
        origins.forEach((layer,origin) -> {
            mutable.getAllFromSource(powerSource(layer)).forEach(power -> {
                if (!origin.powers().elements().contains(power)){
                    mutable.revoke(power.id(),powerSource(layer));
                }
            });
        });
        mutable.close();
    }
    public static ResourceLocation powerSource(OriginLayer layer){
        return OriginLayerManager.getInstance().getKey(layer);
    }

    static {
        ServerEntityEvents.ENTITY_LOAD.register(FoxoOrigins.id("origins_attachment"), FoxoOriginsAttachment::onLoad);
    }
    private static void onLoad(Entity entity, ServerLevel level){
        Map<OriginLayer,Origin> data = entity.getAttachedOrSet(FoxoOriginsRegistry.ORIGIN_ATTACHMENT, ImmutableMap.of());
        refreshOrigins(entity, data);
    }
}
