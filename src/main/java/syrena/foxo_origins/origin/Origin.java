package syrena.foxo_origins.origin;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.kaya.condition.Condition;
import io.github.eggohito.kaya.context.Context;
import io.github.eggohito.kaya.context.ContextValidatable;
import io.github.eggohito.kaya.context.ContextValidator;
import io.github.eggohito.kaya.power.PowerHolder;
import io.github.eggohito.kaya.power.manager.PowerManager;
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


public record Origin(Component name, Component description, Component shortDescription, Either<ResourceLocation,ItemStack> icon, int impact, int order, TagLike<PowerHolder<?>> powers, Either<Boolean,Condition> visible, Either<Boolean, Condition> choosable, Either<Boolean, Condition> allowRandom, int loading_priority) implements ContextValidatable {
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
                            Codec.either(Codec.BOOL, Condition.CODEC).optionalFieldOf("visible",Either.left(true)).forGetter(Origin::visible),
                            Codec.either(Codec.BOOL, Condition.CODEC).optionalFieldOf("choosable",Either.left(true)).forGetter(Origin::choosable),
                            Codec.either(Codec.BOOL, Condition.CODEC).optionalFieldOf("allow_random",Either.left(true)).forGetter(Origin::allowRandom),
                            Codec.INT.optionalFieldOf("loading_priority",0).forGetter(Origin::loading_priority))
                    .apply(instance,Origin::new)
    );

    public static final Codec<Origin> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, Origin> STREAM_CODEC = MiscUtil.composite11(
            ComponentSerialization.STREAM_CODEC, Origin::name,
            ComponentSerialization.STREAM_CODEC, Origin::description,
            ComponentSerialization.STREAM_CODEC, Origin::shortDescription,
            ByteBufCodecs.either(ResourceLocation.STREAM_CODEC,ItemStack.STREAM_CODEC),Origin::icon,
            ByteBufCodecs.INT,Origin::impact,
            ByteBufCodecs.INT,Origin::order,
            TagLike.streamCodec(PowerManager.TAG_LOOKUP),Origin::powers,
            ByteBufCodecs.either(ByteBufCodecs.BOOL,Condition.STREAM_CODEC),Origin::visible,
            ByteBufCodecs.either(ByteBufCodecs.BOOL,Condition.STREAM_CODEC),Origin::choosable,
            ByteBufCodecs.either(ByteBufCodecs.BOOL,Condition.STREAM_CODEC),Origin::allowRandom,
            ByteBufCodecs.INT,Origin::loading_priority,
            Origin::new
    );


    public boolean isVisible(Context context) {
        return this.visible.right().map(condition -> condition.test(context)).orElse(this.visible().left().get());
    }
    public boolean isChoosable(Context context) {
        return this.choosable.right().map(condition -> condition.test(context)).orElse(this.visible().left().get());
    }
    public boolean allowsRandom(Context context) {
        return this.allowRandom.right().map(condition -> condition.test(context)).orElse(this.visible().left().get());
    }
    @Override
    public @NotNull String toString() {
        return "";
    }

    @Override
    public void validate(ContextValidator validator) {
        this.allowRandom.ifRight(allowRandom -> allowRandom.validate(validator.forChild(".allow_random_condition")));
        this.choosable.ifRight(choosableCondition -> choosableCondition.validate(validator.forChild(".choosable_condition")));
        this.visible.ifRight(visibleCondition -> visibleCondition.validate(validator.forChild(".visible_condition")));
    }
}
