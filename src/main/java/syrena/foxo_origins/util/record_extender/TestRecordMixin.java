package syrena.foxo_origins.util.record_extender;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TestRecord.class)
public class TestRecordMixin {
    @Unique
    private final MergedRecord ours = MergedRecord.CTOR_PARAM.orElse(/* default values*/ new MergedRecord(0, 0));
    @Final
    @Shadow
    public static Codec<TestRecord> CODEC;

    static {
        var originalCodec = CODEC;
        CODEC = MergedRecord.CODEC.codec().dispatch(test -> ((TestRecordMixin) (Object) test).ours, ours -> new ScopedValueComposeMapCodec<>(new HashDivergentMapCodec<>(
                MapCodec.assumeMapUnsafe(originalCodec),
                new OpsSwitchMapCodec<>(JsonOps.INSTANCE, MapCodec.assumeMapUnsafe(originalCodec))
        ), MergedRecord.CTOR_PARAM, ours));
    }
}
