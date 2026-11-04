package syrena.foxo_origins.origin_layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record OriginLayerHolder(ResourceLocation id, OriginLayer layer) {
    public static String ID_KEY = "id";
    public static String LAYER_KEY = "origin_layer";
    public static final MapCodec<OriginLayerHolder> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                                ResourceLocation.CODEC.fieldOf(ID_KEY).forGetter(OriginLayerHolder::id),
                                OriginLayer.CODEC.fieldOf(LAYER_KEY).forGetter(OriginLayerHolder::layer))
                        .apply(instance, OriginLayerHolder::new)
    );

    public static final Codec<OriginLayerHolder> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, OriginLayerHolder> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, OriginLayerHolder::id,
            OriginLayer.STREAM_CODEC,OriginLayerHolder::layer,
            OriginLayerHolder::new
    );
}
