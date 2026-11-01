package syrena.foxo_origins.util.record_extender;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.util.HashOps;

import java.util.stream.Stream;

/**
 * A map codec that behaves differently when the input ops is {@link net.minecraft.util.HashOps}
 *
 * @param <T>
 */
public final class HashDivergentMapCodec<T> extends com.mojang.serialization.MapCodec<T> {
    private final com.mojang.serialization.MapCodec<T> unhashed;
    private final com.mojang.serialization.MapCodec<T> hashed;

    public HashDivergentMapCodec(com.mojang.serialization.MapCodec<T> unhashed, com.mojang.serialization.MapCodec<T> hashed) {
        this.unhashed = unhashed;
        this.hashed = hashed;
    }

    private static boolean isHash(DynamicOps<?> ops) {
        return switch (ops) {
            case HashOps a -> true;
            case DelegatingOps<?> delegating -> isHash(delegating.delegate);
            default -> false;
        };
    }

    public com.mojang.serialization.MapCodec<T> hashed() {
        return hashed;
    }

    public com.mojang.serialization.MapCodec<T> unhashed() {
        return unhashed;
    }

    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        if (isHash(ops)) {
            return hashed.keys(ops);
        }
        return unhashed.keys(ops);
    }

    @Override
    public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
        if (isHash(ops)) {
            return hashed.decode(ops, input);
        }
        return unhashed.decode(ops, input);
    }

    @Override
    public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
        if (isHash(ops)) {
            return hashed.encode(input, ops, prefix);
        }
        return unhashed.encode(input, ops, prefix);
    }
}
