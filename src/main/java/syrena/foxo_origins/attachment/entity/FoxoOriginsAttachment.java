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
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoxoOriginsAttachment {
    public static final Codec<Map<Holder<Origin>,Holder<OriginLayer>>> CODEC = Codec.unboundedMap(FoxoOriginsCodecs.ORIGIN_HOLDER_CODEC,FoxoOriginsCodecs.ORIGIN_LAYER_HOLDER_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<Holder<Origin>, Holder<OriginLayer>>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new, // Constructs a map with the specified capacity
                    ByteBufCodecs.holder(FoxoOriginsRegistry.ORIGIN,Origin.STREAM_CODEC),
                    ByteBufCodecs.holder(FoxoOriginsRegistry.ORIGIN_LAYER,OriginLayer.STREAM_CODEC),
                    256 // The map can only have up to 256 elements
            );
    public static void refreshOrigins(Entity entity, Map<Holder<Origin>,Holder<OriginLayer>> origins){
        MutablePowers mutable = MutablePowers.create(entity);
        origins.forEach((origin,layer) -> {
            origin.value().powers().elements().forEach(power -> {
                mutable.grant(power.id(),powerSource(layer,entity));
            });
        });
        origins.forEach((origin,layer) -> {
            mutable.getAllFromSource(powerSource(layer,entity)).forEach(power -> {
                if (!origin.value().powers().contains(power)){
                    mutable.revoke(power.id(),powerSource(layer,entity));
                }
            });
        });
        mutable.close();
    }
    public static ResourceLocation powerSource(Holder<OriginLayer> layer, Entity entity){
        return entity.level().registryAccess().getOrThrow(FoxoOriginsRegistry.ORIGIN_LAYER).value().getKey(layer.value());
    }
}
