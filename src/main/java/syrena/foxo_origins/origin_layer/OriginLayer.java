package syrena.foxo_origins.origin_layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.ContextValidatable;
import io.github.eggohito.neo_apoli.context.ContextValidator;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ints.ConstantIntProvider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.tag.TagLike;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.codec.FoxoOriginsCodecs;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;
import syrena.foxo_origins.util.MiscUtil;

import java.util.List;
import java.util.Optional;


public record OriginLayer(Component name, Component description, Component short_description, ItemStack icon, IntProvider order, TagLike<Origin> origins, Optional<Origin> defaultOrigin, BooleanProvider enabled, BooleanProvider allowRandom, IntProvider loading_priority)  {
    static ContextValidator.Parameters PARAMETERS = new ContextValidator.Parameters.Builder()
            .required(NeoApoliContextParameters.THIS_ENTITY)
            .build();
    public static final MapCodec<OriginLayer> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                            ComponentSerialization.CODEC.fieldOf("name").forGetter(OriginLayer::name),
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(OriginLayer::description),
                            ComponentSerialization.CODEC.fieldOf("short_description").forGetter(OriginLayer::short_description),
                            ItemStack.CODEC.fieldOf("icon").forGetter(OriginLayer::icon),
                            IntProvider.CODEC.optionalFieldOf("order", new ConstantIntProvider(0)).forGetter(OriginLayer::order),
                            TagLike.createCodec(OriginManager.TAG_LOOKUP).fieldOf("origins").forGetter(OriginLayer::origins),
                            Origin.CODEC.optionalFieldOf("default_origin").forGetter(OriginLayer::defaultOrigin),
                            BooleanProvider.CODEC.optionalFieldOf("enabled",new ConstantBooleanProvider(true)).forGetter(OriginLayer::enabled),
                            BooleanProvider.CODEC.optionalFieldOf("allow_random",new ConstantBooleanProvider(true)).forGetter(OriginLayer::allowRandom),
                            IntProvider.CODEC.optionalFieldOf("loading_priority", new ConstantIntProvider(0)).forGetter(OriginLayer::loading_priority))
                    .apply(instance, OriginLayer::new)

    );

    public static final Codec<OriginLayer> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, OriginLayer> STREAM_CODEC = MiscUtil.composite10(
            ComponentSerialization.STREAM_CODEC, OriginLayer::name,
            ComponentSerialization.STREAM_CODEC, OriginLayer::description,
            ComponentSerialization.STREAM_CODEC, OriginLayer::short_description,
            ItemStack.STREAM_CODEC, OriginLayer::icon,
            IntProvider.STREAM_CODEC, OriginLayer::order,
            TagLike.createStreamCodec(OriginManager.TAG_LOOKUP),OriginLayer::origins,
            ByteBufCodecs.optional(Origin.STREAM_CODEC),OriginLayer::defaultOrigin,
            BooleanProvider.STREAM_CODEC, OriginLayer::enabled,
            BooleanProvider.STREAM_CODEC, OriginLayer::allowRandom,
            IntProvider.STREAM_CODEC, OriginLayer::loading_priority,
            OriginLayer::new
    );

}