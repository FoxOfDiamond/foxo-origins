package syrena.foxo_origins.client.network;


import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import syrena.foxo_origins.network.packet.HandshakePacket;

import java.util.NoSuchElementException;

public final class FoxoOriginsClientboundPacketListener {

    public static void init() {

        //ClientConfigurationNetworking.registerGlobalReceiver(HandshakePacket.TYPE, FoxoOriginsClientboundPacketListener::sendHandshakeReply);

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            //ClientPlayNetworking.registerReceiver(ClientboundUpdateOriginsPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
        });

    }

    private static void sendHandshakeReply(HandshakePacket payload, ClientConfigurationNetworking.Context context) {

        try {

            String modId = payload.modId();
            ModContainer mod = FabricLoader.getInstance()
                    .getModContainer(modId)
                    .orElseThrow();

            context.responseSender().sendPacket(new HandshakePacket(modId, mod.getMetadata().getVersion().getFriendlyString()));

        }

        catch (NoSuchElementException ignored) {
            context.responseSender().disconnect(payload.createMissingModComponent());
        }

    }

}
