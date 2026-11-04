package syrena.foxo_origins.attachment.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.kaya.power.entity.MutablePowers;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.origin.OriginHolder;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin_layer.OriginLayerHolder;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.HashMap;
import java.util.Map;

public class FoxoOriginsAttachment {
    public static final ResourceLocation DEFAULT_PLACEHOLDER = ResourceLocation.fromNamespaceAndPath("foxo-origins","placeholder_invulnerable");
    public static final Codec<Map<OriginLayerHolder, OriginHolder>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC.flatXmap(OriginLayerManager.getInstance()::getAsResult,OriginLayerManager.getInstance()::getKeyAsResult),ResourceLocation.CODEC.flatXmap(OriginManager.getInstance()::getAsResult,OriginManager.getInstance()::getKeyAsResult));
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<OriginLayerHolder, OriginHolder>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new, // Constructs a map with the specified capacity
                    ResourceLocation.STREAM_CODEC.map(OriginLayerManager.getInstance()::get,OriginLayerManager.getInstance()::getKey),
                    ResourceLocation.STREAM_CODEC.map(OriginManager.getInstance()::get,OriginManager.getInstance()::getKey),
                    256 // The map can only have up to 256 elements
            );
    public static void refreshOrigins(Entity entity, Map<OriginLayerHolder, OriginHolder> origins){
        for (OriginLayerHolder layer: OriginLayerManager.getInstance().values()){
            if (!origins.containsKey(layer)){
                if (layer.layer().defaultOrigin().isPresent()){
                    HashMap<OriginLayerHolder, OriginHolder> mutableAttachment = new HashMap<>(origins);

                    mutableAttachment.put(layer,layer.layer().defaultOrigin().get());
                    entity.setAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT, ImmutableMap.copyOf(mutableAttachment));
                    refreshOrigins(entity, entity.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT));
                    return;
                } else {
                    DataResult<OriginHolder> placeholderOrigin = OriginManager.getInstance().getAsResult(DEFAULT_PLACEHOLDER);
                    if (!placeholderOrigin.isSuccess()) return;
                    OriginHolder placeholder = layer.layer().placeholderOrigin().orElse(placeholderOrigin.getOrThrow());
                    HashMap<OriginLayerHolder, OriginHolder> mutableAttachment = new HashMap<>(origins);

                    mutableAttachment.put(layer,placeholder);
                    entity.setAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT, ImmutableMap.copyOf(mutableAttachment));
                    refreshOrigins(entity, entity.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT));
                    return;
                    //Handle force player choice packets
                }
            }
        }
        MutablePowers mutable = MutablePowers.create(entity);
        origins.forEach((layer,origin) -> {
            FoxoOrigins.LOGGER.info(layer.toString());
            //FoxoOrigins.LOGGER.info(OriginLayerManager.getInstance().getKey(origin).toString());

            origin.origin().powers().elements().forEach(power -> {
                mutable.grant(power.id(),powerSource(layer));
            });
        });
        origins.forEach((layer,origin) -> {
            mutable.getAllFromSource(powerSource(layer)).forEach(power -> {
                if (!origin.origin().powers().elements().contains(power)){
                    mutable.revoke(power.id(),powerSource(layer));
                }
            });
        });
        mutable.close();
    }
    public static ResourceLocation powerSource(OriginLayerHolder layer){
        return layer.id();
    }

    static {
        ServerEntityEvents.ENTITY_LOAD.register(FoxoOrigins.id("origins_attachment"), FoxoOriginsAttachment::onLoad);
    }
    private static void onLoad(Entity entity, ServerLevel level){
        Map<OriginLayerHolder, OriginHolder> data = entity.getAttachedOrSet(FoxoOriginsRegistry.ORIGIN_ATTACHMENT, ImmutableMap.of());
        refreshOrigins(entity, data);
    }
}
