package syrena.foxo_origins.origin;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.kaya.context.Context;
import io.github.eggohito.kaya.context.ContextValidatable;
import io.github.eggohito.kaya.context.ContextValidator;
import io.github.eggohito.kaya.power.PowerHolder;
import io.github.eggohito.kaya.power.manager.PowerManager;
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
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.util.MiscUtil;


public record Origin(Component name, Component description, Component shortDescription, Either<ResourceLocation,ItemStack> icon, int order, int impact, TagLike<PowerHolder<?>> powers, BooleanProvider visible, BooleanProvider choosable, BooleanProvider allowRandom, int loading_priority) implements ContextValidatable {
    static ContextValidator.Parameters PARAMETERS = new ContextValidator.Parameters.Builder()
            .required(KayaContextParameters.THIS_ENTITY)
            .build();
    public static final MapCodec<Origin> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                            ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(Origin::name),
                            ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(Origin::description),
                            ComponentSerialization.CODEC.optionalFieldOf("short_description", Component.empty()).forGetter(Origin::shortDescription),
                            Codec.either(ResourceLocation.CODEC,ItemStack.CODEC).optionalFieldOf("icon",null).forGetter(Origin::icon),
                            Codec.INT.optionalFieldOf("order",0).forGetter(Origin::order),
                            Codec.INT.optionalFieldOf("impact", 0).forGetter(Origin::impact),
                            TagLike.codec(PowerManager.TAG_LOOKUP).optionalFieldOf("powers",TagLike.empty()).forGetter(Origin::powers),
                            BooleanProvider.CODEC.optionalFieldOf("visible",new ConstantBooleanProvider(true)).forGetter(Origin::visible),
                            BooleanProvider.CODEC.optionalFieldOf("choosable",new ConstantBooleanProvider(true)).forGetter(Origin::choosable),
                            BooleanProvider.CODEC.optionalFieldOf("allow_random",new ConstantBooleanProvider(true)).forGetter(Origin::allowRandom),
                            Codec.INT.optionalFieldOf("loading_priority",0).forGetter(Origin::loading_priority))
                    .apply(instance,Origin::new)
    );

    public static final Codec<Origin> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, Origin> STREAM_CODEC = MiscUtil.composite11(
            ComponentSerialization.STREAM_CODEC, Origin::name,
            ComponentSerialization.STREAM_CODEC, Origin::description,
            ComponentSerialization.STREAM_CODEC, Origin::shortDescription,
            ByteBufCodecs.either(ResourceLocation.STREAM_CODEC,ItemStack.STREAM_CODEC),Origin::icon,
            ByteBufCodecs.INT,Origin::order,
            ByteBufCodecs.INT,Origin::impact,
            TagLike.streamCodec(PowerManager.TAG_LOOKUP),Origin::powers,
            BooleanProvider.STREAM_CODEC,Origin::visible,
            BooleanProvider.STREAM_CODEC,Origin::choosable,
            BooleanProvider.STREAM_CODEC,Origin::allowRandom,
            ByteBufCodecs.INT,Origin::loading_priority,
            Origin::new
    );


    public boolean isVisible(Context context) {
        return this.visible.getBoolean(context);
    }
    public boolean isChoosable(Context context) {
        return this.choosable.getBoolean(context);
    }
    public boolean allowsRandom(Context context) {
        return this.allowRandom.getBoolean(context);
    }
    @Override
    public @NotNull String toString() {
        return "";
    }

    @Override
    public void validate(ContextValidator validator) {
        this.allowRandom.validate(validator.forChild(".allow_random_condition"));
        this.choosable.validate(validator.forChild(".choosable_condition"));
        this.visible.validate(validator.forChild(".visible_condition"));
    }
}
