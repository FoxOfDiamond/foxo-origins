package syrena.foxo_origins.origin_layer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.kaya.context.Context;
import io.github.eggohito.kaya.context.ContextValidatable;
import io.github.eggohito.kaya.context.ContextValidator;
import io.github.eggohito.kaya.provider.custom.bool.BooleanProvider;
import io.github.eggohito.kaya.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.kaya.registry.context.KayaContextParameters;
import io.github.eggohito.kaya.util.tag.TagLike;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.util.MiscUtil;

import java.util.Optional;


public record OriginLayer(Component name, Component description, Component shortDescription, Optional<Either<ResourceLocation,ItemStack>> icon, int order, int impact, TagLike<Origin> origins, Optional<Origin> defaultOrigin, Optional<Origin> placeholderOrigin, BooleanProvider visible, BooleanProvider allowRandom, int loading_priority) implements ContextValidatable {
    static ContextValidator.Parameters PARAMETERS = new ContextValidator.Parameters.Builder()
            .required(KayaContextParameters.THIS_ENTITY)
            .build();
    public static final MapCodec<OriginLayer> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                            ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(OriginLayer::name),
                            ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(OriginLayer::description),
                            ComponentSerialization.CODEC.optionalFieldOf("short_description", Component.empty()).forGetter(OriginLayer::shortDescription),
                            Codec.either(ResourceLocation.CODEC,ItemStack.CODEC).optionalFieldOf("icon").forGetter(OriginLayer::icon),
                            Codec.INT.optionalFieldOf("order",0).forGetter(OriginLayer::order),
                            Codec.INT.optionalFieldOf("impact", 0).forGetter(OriginLayer::impact),
                            TagLike.codec(OriginManager.TAG_LOOKUP).optionalFieldOf("origins",TagLike.empty()).forGetter(OriginLayer::origins),
                            Origin.CODEC.optionalFieldOf("default_origin").forGetter(OriginLayer::defaultOrigin),
                            Origin.CODEC.optionalFieldOf("placeholder_origin").forGetter(OriginLayer::placeholderOrigin),
                            BooleanProvider.CODEC.optionalFieldOf("visible", new ConstantBooleanProvider(true)).forGetter(OriginLayer::visible),
                            BooleanProvider.CODEC.optionalFieldOf("allow_random",new ConstantBooleanProvider(true)).forGetter(OriginLayer::allowRandom),
                            Codec.INT.optionalFieldOf("loading_priority", 0).forGetter(OriginLayer::loading_priority))
                    .apply(instance, OriginLayer::new)

    );

    public static final Codec<OriginLayer> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, OriginLayer> STREAM_CODEC = MiscUtil.composite12(
            ComponentSerialization.STREAM_CODEC, OriginLayer::name,
            ComponentSerialization.STREAM_CODEC, OriginLayer::description,
            ComponentSerialization.STREAM_CODEC, OriginLayer::shortDescription,
            ByteBufCodecs.optional(ByteBufCodecs.either(ResourceLocation.STREAM_CODEC,ItemStack.STREAM_CODEC)), OriginLayer::icon,
            ByteBufCodecs.INT, OriginLayer::order,
            ByteBufCodecs.INT, OriginLayer::impact,
            TagLike.streamCodec(OriginManager.TAG_LOOKUP),OriginLayer::origins,
            ByteBufCodecs.optional(Origin.STREAM_CODEC),OriginLayer::defaultOrigin,
            ByteBufCodecs.optional(Origin.STREAM_CODEC),OriginLayer::placeholderOrigin,
            BooleanProvider.STREAM_CODEC, OriginLayer::visible,
            BooleanProvider.STREAM_CODEC, OriginLayer::allowRandom,
            ByteBufCodecs.INT, OriginLayer::loading_priority,
            OriginLayer::new
    );

    public boolean isVisible(Context context) {
        return this.visible.getBoolean(context);
    }
    public boolean allowsRandom(Context context) {
        return this.allowRandom.getBoolean(context);
    }
    @Override
    public void validate(ContextValidator validator) {
        this.allowRandom.validate(validator.forChild(".allow_random_condition"));
        this.visible.validate(validator.forChild(".visible_condition"));
    }
}