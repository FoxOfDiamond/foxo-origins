package syrena.foxo_origins.util.record_extender;

import com.mojang.serialization.*;
import net.minecraft.resources.RegistryOps;

import java.util.stream.Stream;

public class OpsSwitchMapCodec<T, V> extends MapCodec<T> {
    private final DynamicOps<V> targetOps;
    private final MapCodec<T> inner;

    public OpsSwitchMapCodec(DynamicOps<V> targetOps, MapCodec<T> inner) {
        this.targetOps = targetOps;
        this.inner = inner;
    }

    private DynamicOps<V> getTargetOpsFor(DynamicOps<?> ops) {
        if (ops instanceof RegistryOps<?> registryOps) return registryOps.withParent(this.targetOps);
        else return this.targetOps;
    }

    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        DynamicOps<V> targetOps = getTargetOpsFor(ops);
        return inner.keys(targetOps).map(value -> targetOps.convertTo(ops, value));
    }

    @Override
    public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
        DynamicOps<V> targetOps = getTargetOpsFor(ops);
        return targetOps.getMap(ops.convertTo(targetOps, ops.createMap(input.entries()))).flatMap(it -> inner.decode(targetOps, it));
    }

    @Override
    public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
        DynamicOps<V> targetOps = getTargetOpsFor(ops);
        var res = inner.encode(input, targetOps, targetOps.mapBuilder()).build(targetOps.emptyMap()).flatMap(targetOps::getMap);
        prefix.withErrorsFrom(res);
        res.ifSuccess(map -> map.entries().forEach(pair -> prefix.add(targetOps.convertTo(ops, pair.getFirst()), targetOps.convertTo(ops, pair.getSecond()))));
        return prefix;
    }
}
