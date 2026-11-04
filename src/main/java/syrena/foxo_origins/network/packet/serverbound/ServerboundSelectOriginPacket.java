package syrena.foxo_origins.network.packet.serverbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.FoxoOrigins;

import java.util.Optional;

public record ServerboundSelectOriginPacket(ResourceLocation layer, Optional<ResourceLocation> origin) implements CustomPacketPayload {
    // 0: randomize origin
    // 1: select origin
    public static final Type<ServerboundSelectOriginPacket> TYPE = new Type<>(FoxoOrigins.id("gui_handler/").withPath(path -> "serverbound/" + path + "/select"));
    public static final StreamCodec<ByteBuf, ServerboundSelectOriginPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ServerboundSelectOriginPacket::layer,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), ServerboundSelectOriginPacket::origin,
            ServerboundSelectOriginPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
