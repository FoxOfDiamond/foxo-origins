package syrena.foxo_origins.util.record_extender;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.kaya.provider.custom.number.IntProvider;
import io.github.eggohito.kaya.provider.custom.number.ints.ConstantIntProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ExtendedPowerHolder(Component shortDescription, Either<ResourceLocation, ItemStack> icon, IntProvider impact, IntProvider order) {
    public static final ScopedValue<ExtendedPowerHolder> CTOR_PARAM = ScopedValue.newInstance();
    public static final MapCodec<ExtendedPowerHolder> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("shortDescription",Component.empty()).forGetter(ExtendedPowerHolder::shortDescription),
            Codec.either(ResourceLocation.CODEC,ItemStack.CODEC).optionalFieldOf("icon",null).forGetter(ExtendedPowerHolder::icon),
            IntProvider.CODEC.optionalFieldOf("impact",new ConstantIntProvider(0)).forGetter(ExtendedPowerHolder::impact),
            IntProvider.CODEC.optionalFieldOf("order",new ConstantIntProvider(0)).forGetter(ExtendedPowerHolder::order)
    ).apply(instance, ExtendedPowerHolder::new));
}
