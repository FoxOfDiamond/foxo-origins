package syrena.foxo_origins.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import syrena.foxo_origins.network.packet.clientbound.ClientboundUpdateOriginLayersPacket;
import syrena.foxo_origins.network.packet.clientbound.ClientboundUpdateOriginsPacket;

public class FoxoOriginsPackets {
    public static void registerAll(){
        //PayloadTypeRegistry.configurationS2C().register(HandshakePacket.TYPE, HandshakePacket.CODEC);

        PayloadTypeRegistry.playS2C().register(ClientboundUpdateOriginsPacket.TYPE,  ClientboundUpdateOriginsPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateOriginLayersPacket.TYPE,  ClientboundUpdateOriginLayersPacket.CODEC);
    }
}
