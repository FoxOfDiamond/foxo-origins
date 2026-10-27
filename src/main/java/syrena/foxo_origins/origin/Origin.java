package syrena.foxo_origins.origin;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.context.ContextValidatable;
import io.github.eggohito.neo_apoli.context.ContextValidator;
import io.github.eggohito.neo_apoli.hud.Sprite;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ints.ConstantIntProvider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParameterTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.ParsedArgument;
import io.github.eggohito.neo_apoli.util.tag.TagLike;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.util.MiscUtil;

import java.util.List;
import java.util.Optional;

import static syrena.foxo_origins.codec.FoxoOriginsCodecs.POWER_OR_TAG_ARGUMENT;

public record Origin(Component name, Component description, Component short_description, ItemStack icon, IntProvider impact, IntProvider order, TagLike<PowerHolder<?>> powers, Optional<Condition> visibleCondition, Optional<Condition> choosableCondition, IntProvider loading_priority) implements ContextValidatable {
    static ContextValidator.Parameters PARAMETERS = new ContextValidator.Parameters.Builder()
            .required(NeoApoliContextParameters.THIS_ENTITY)
            .build();
    public static final MapCodec<Origin> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                            ComponentSerialization.CODEC.fieldOf("name").forGetter(Origin::name),
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(Origin::description),
                            ComponentSerialization.CODEC.fieldOf("short_description").forGetter(Origin::short_description),
                            ItemStack.CODEC.fieldOf("icon").forGetter(Origin::icon),
                            IntProvider.CODEC.optionalFieldOf("order",new ConstantIntProvider(0)).forGetter(Origin::order),
                            IntProvider.CODEC.fieldOf("impact").forGetter(Origin::impact),
                            TagLike.createCodec(PowerManager.TAG_LOOKUP).fieldOf("powers").forGetter(Origin::powers),
                            Condition.CODEC.optionalFieldOf("visible_condition").forGetter(Origin::visibleCondition),
                            Condition.CODEC.optionalFieldOf("choosable_condition").forGetter(Origin::choosableCondition),                           IntProvider.CODEC.optionalFieldOf("loading_priority",new ConstantIntProvider(0)).forGetter(Origin::loading_priority))
                    .apply(instance,Origin::new)
    );

    public static final Codec<Origin> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, Origin> STREAM_CODEC = MiscUtil.composite10(
            ComponentSerialization.STREAM_CODEC, Origin::name,
            ComponentSerialization.STREAM_CODEC, Origin::description,
            ComponentSerialization.STREAM_CODEC, Origin::short_description,
            ItemStack.STREAM_CODEC,Origin::icon,
            IntProvider.STREAM_CODEC,Origin::impact,
            IntProvider.STREAM_CODEC,Origin::order,
            TagLike.createStreamCodec(PowerManager.TAG_LOOKUP),Origin::powers,
            ByteBufCodecs.optional(Condition.STREAM_CODEC),Origin::visibleCondition,
            ByteBufCodecs.optional(Condition.STREAM_CODEC),Origin::choosableCondition,
            IntProvider.STREAM_CODEC,Origin::loading_priority,
            Origin::new
    );


    public boolean isVisible(Context context) {
        return this.visibleCondition.map(condition -> condition.test(context)).orElse(true);
    }
    public boolean isChoosable(Context context) {
        return this.choosableCondition.map(condition -> condition.test(context)).orElse(true);
    }
    @Override
    public @NotNull String toString() {
        return "";
    }

    @Override
    public void validate(ContextValidator validator) {
        this.choosableCondition().ifPresent(choosableCondition -> choosableCondition.validate(validator.forChild(".choosable_condition")));
        this.visibleCondition().ifPresent(visibleCondition -> visibleCondition.validate(validator.forChild(".visible_condition")));
    }
}
