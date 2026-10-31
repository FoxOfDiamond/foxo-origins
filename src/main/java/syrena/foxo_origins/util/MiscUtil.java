package syrena.foxo_origins.util;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class MiscUtil {
    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, C> composite10(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> function7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> function8, final StreamCodec<? super B, T9> streamCodec9, final Function<C, T9> function9,final StreamCodec<? super B, T10> streamCodec10, final Function<C, T10> function10, final Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> function102) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = (T1)streamCodec.decode(object);
                T2 object3 = (T2)streamCodec2.decode(object);
                T3 object4 = (T3)streamCodec3.decode(object);
                T4 object5 = (T4)streamCodec4.decode(object);
                T5 object6 = (T5)streamCodec5.decode(object);
                T6 object7 = (T6)streamCodec6.decode(object);
                T7 object8 = (T7)streamCodec7.decode(object);
                T8 object9 = (T8)streamCodec8.decode(object);
                T9 object10 = (T9)streamCodec9.decode(object);
                T10 object11 = (T10)streamCodec10.decode(object);
                return (C) function102.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10,object11);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
                streamCodec3.encode(object, function3.apply(object2));
                streamCodec4.encode(object, function4.apply(object2));
                streamCodec5.encode(object, function5.apply(object2));
                streamCodec6.encode(object, function6.apply(object2));
                streamCodec7.encode(object, function7.apply(object2));
                streamCodec8.encode(object, function8.apply(object2));
                streamCodec9.encode(object, function9.apply(object2));
                streamCodec10.encode(object, function10.apply(object2));
            }
        };
    }
    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> StreamCodec<B, C> composite11(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> function7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> function8, final StreamCodec<? super B, T9> streamCodec9, final Function<C, T9> function9,final StreamCodec<? super B, T10> streamCodec10, final Function<C, T10> function10,final StreamCodec<? super B, T11> streamCodec11, final Function<C, T11> function11, final Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, C> function112) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = (T1)streamCodec.decode(object);
                T2 object3 = (T2)streamCodec2.decode(object);
                T3 object4 = (T3)streamCodec3.decode(object);
                T4 object5 = (T4)streamCodec4.decode(object);
                T5 object6 = (T5)streamCodec5.decode(object);
                T6 object7 = (T6)streamCodec6.decode(object);
                T7 object8 = (T7)streamCodec7.decode(object);
                T8 object9 = (T8)streamCodec8.decode(object);
                T9 object10 = (T9)streamCodec9.decode(object);
                T10 object11 = (T10)streamCodec10.decode(object);
                T11 object12 = (T11)streamCodec11.decode(object);
                return (C) function112.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10,object11,object12);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
                streamCodec3.encode(object, function3.apply(object2));
                streamCodec4.encode(object, function4.apply(object2));
                streamCodec5.encode(object, function5.apply(object2));
                streamCodec6.encode(object, function6.apply(object2));
                streamCodec7.encode(object, function7.apply(object2));
                streamCodec8.encode(object, function8.apply(object2));
                streamCodec9.encode(object, function9.apply(object2));
                streamCodec10.encode(object, function10.apply(object2));
                streamCodec11.encode(object, function11.apply(object2));
            }
        };
    }
}
