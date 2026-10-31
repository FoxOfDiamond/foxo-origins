package syrena.foxo_origins.client.origin_layer.manager;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import syrena.foxo_origins.network.packet.clientbound.ClientboundUpdateOriginLayersPacket;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.origin_layer.ServerOriginLayerManager;

public class ClientOriginLayerManager extends ServerOriginLayerManager {

    @Override
    public void init() {

        super.init();

        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateOriginLayersPacket.TYPE, (payload, context) -> this.receive(payload));
        ClientPlayConnectionEvents.DISCONNECT.register(OriginLayerManager.ID, (handler, client) -> this.clear());
    }

    private void receive(ClientboundUpdateOriginLayersPacket payload) {
        this.contents = ImmutableMap.copyOf(payload.originLayers());
        this.tags = ImmutableMap.copyOf(payload.tags());
    }

    private void clear() {
        this.contents = ImmutableMap.of();
        this.tags = ImmutableMap.of();
    }

}
