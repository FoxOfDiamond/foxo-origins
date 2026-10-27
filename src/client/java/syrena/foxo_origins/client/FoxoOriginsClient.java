package syrena.foxo_origins.client;

import net.fabricmc.api.ClientModInitializer;
import syrena.foxo_origins.client.network.FoxoOriginsClientboundPacketListener;
import syrena.foxo_origins.client.util.registry.FoxoOriginsKeyMappings;

public class FoxoOriginsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        FoxoOriginsClientboundPacketListener.init();
        FoxoOriginsKeyMappings.registerAll();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}