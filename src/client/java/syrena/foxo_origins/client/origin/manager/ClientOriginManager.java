package syrena.foxo_origins.client.origin.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdatePowersPacket;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import syrena.foxo_origins.network.packet.clientbound.ClientboundUpdateOriginsPacket;
import syrena.foxo_origins.origin.ServerOriginManager;

public class ClientOriginManager extends ServerOriginManager {

    @Override
    public void init() {

        super.init();

        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateOriginsPacket.TYPE, (payload, context) -> this.receive(payload));
        ClientPlayConnectionEvents.DISCONNECT.register(PowerManager.ID, (handler, client) -> this.clear());

    }

    private void receive(ClientboundUpdateOriginsPacket payload) {
        this.contents = ImmutableMap.copyOf(payload.origins());
        this.tags = ImmutableMap.copyOf(payload.tags());
    }

    private void clear() {
        this.contents = ImmutableMap.of();
        this.tags = ImmutableMap.of();
    }

}
