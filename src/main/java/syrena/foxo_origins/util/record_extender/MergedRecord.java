package syrena.foxo_origins.util.record_extender;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MergedRecord(int ours, int alsoOurs) {
    public static final ScopedValue<MergedRecord> CTOR_PARAM = ScopedValue.newInstance();
    public static final MapCodec<MergedRecord> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ours").forGetter(MergedRecord::ours),
            Codec.INT.fieldOf("alsoOurs").forGetter(MergedRecord::alsoOurs)
    ).apply(instance, MergedRecord::new));
}
