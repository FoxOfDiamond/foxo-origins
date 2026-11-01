package syrena.foxo_origins.util.record_extender;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record TestRecord() {
    public static final Codec<TestRecord> CODEC = MapCodec.unit(TestRecord::new).codec();

}
