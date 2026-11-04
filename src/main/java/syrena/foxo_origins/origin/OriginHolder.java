package syrena.foxo_origins.origin;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record OriginHolder(ResourceLocation id, Origin origin) {
    public static String ID_KEY = "id";
    public static String ORIGIN_KEY = "origin";
    public static final MapCodec<OriginHolder> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                            ResourceLocation.CODEC.fieldOf(ID_KEY).forGetter(OriginHolder::id),
                            Origin.CODEC...forGetter(OriginHolder::origin))
                    .apply(instance, OriginHolder::new)
    );

    public static final Codec<OriginHolder> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, OriginHolder> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, OriginHolder::id,
            Origin.STREAM_CODEC, OriginHolder::origin,
            OriginHolder::new
    );
}
