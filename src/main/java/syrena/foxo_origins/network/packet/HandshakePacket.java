package syrena.foxo_origins.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.network.HandshakeTask;

public record HandshakePacket(String modId, String modVersion) implements CustomPacketPayload {

    public static final Type<HandshakePacket> TYPE = new Type<>(FoxoOrigins.id("handshake"));
    public static final StreamCodec<ByteBuf, HandshakePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HandshakePacket::modId,
            ByteBufCodecs.STRING_UTF8, HandshakePacket::modVersion,
            HandshakePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public Component createMissingModComponent() {
        return HandshakeTask.createMissingModComponent(this.modId(), this.modVersion());
    }

}
