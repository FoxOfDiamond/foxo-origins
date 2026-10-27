package syrena.foxo_origins.attachment.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.entity.MutablePowers;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import syrena.foxo_origins.codec.FoxoOriginsCodecs;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoxoOriginsAttachment {
    public static final Codec<Map<Origin,OriginLayer>> CODEC = Codec.unboundedMap(Origin.CODEC,OriginLayer.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<Origin, OriginLayer>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new, // Constructs a map with the specified capacity
                    Origin.STREAM_CODEC,
                    OriginLayer.STREAM_CODEC,
                    256 // The map can only have up to 256 elements
            );
    public static void refreshOrigins(Entity entity, Map<Origin,OriginLayer> origins){
        MutablePowers mutable = MutablePowers.create(entity);
        origins.forEach((origin,layer) -> {
            origin.powers().elements().forEach(power -> {
                mutable.grant(power.id(),powerSource(layer));
            });
        });
        origins.forEach((origin,layer) -> {
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
}
