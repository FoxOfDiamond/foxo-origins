package syrena.foxo_origins.mixin.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.kaya.power.PowerHolder;
import io.github.eggohito.kaya.provider.custom.number.ints.ConstantIntProvider;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import syrena.foxo_origins.util.record_extender.*;

@Mixin(PowerHolder.class)
public class PowerHolderMixin {
    @Unique
    private final ExtendedPowerHolder ours = ExtendedPowerHolder.CTOR_PARAM.orElse(new ExtendedPowerHolder(Component.empty(),null,new ConstantIntProvider(0),new ConstantIntProvider(0)));

    /*@Final
    @Shadow
    public static Codec<PowerHolder<?>> CODEC;
    static {
        Codec<PowerHolder<?>> originalCodec = CODEC;
        CODEC = ExtendedPowerHolder.CODEC.codec().dispatch(test -> ((PowerHolderMixin) (Object) test).ours, ours -> new ScopedValueComposeMapCodec<>(new HashDivergentMapCodec<>(
                MapCodec.assumeMapUnsafe(originalCodec),
                new OpsSwitchMapCodec<>(JsonOps.INSTANCE, MapCodec.assumeMapUnsafe(originalCodec))
        ), ExtendedPowerHolder.CTOR_PARAM, ours));
    }*/
}
