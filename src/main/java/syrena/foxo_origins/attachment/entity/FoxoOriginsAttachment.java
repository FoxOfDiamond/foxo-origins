package syrena.foxo_origins.attachment.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
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
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoxoOriginsAttachment {
    public static final Codec<Map<OriginLayer,Origin>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC.flatXmap(OriginLayerManager.getInstance()::getAsResult,OriginLayerManager.getInstance()::getKeyAsResult),ResourceLocation.CODEC.flatXmap(OriginManager.getInstance()::getAsResult,OriginManager.getInstance()::getKeyAsResult));
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<OriginLayer, Origin>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new, // Constructs a map with the specified capacity
                    ResourceLocation.STREAM_CODEC.map(OriginLayerManager.getInstance()::get,OriginLayerManager.getInstance()::getKey),
                    ResourceLocation.STREAM_CODEC.map(OriginManager.getInstance()::get,OriginManager.getInstance()::getKey),
                    256 // The map can only have up to 256 elements
            );
    public static void refreshOrigins(Entity entity, Map<OriginLayer,Origin> origins){
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
}
