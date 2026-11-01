package syrena.foxo_origins.util.record_extender;

import com.mojang.serialization.*;

import java.util.stream.Stream;

public class ScopedValueComposeMapCodec<T, V> extends MapCodec<T> {
    private final MapCodec<T> inner;
    private final ScopedValue<V> value;
    private final V v;

    public ScopedValueComposeMapCodec(MapCodec<T> inner, ScopedValue<V> value, V v) {
        this.inner = inner;
        this.value = value;
        this.v = v;
    }


    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        return ScopedValue.where(value, v).call(() -> inner.keys(ops));
    }

    @Override
    public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
        return ScopedValue.where(value, v).call(() -> inner.decode(ops, input));
    }

    @Override
    public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
        return ScopedValue.where(value, v).call(() -> inner.encode(input, ops, prefix));
    }
}
