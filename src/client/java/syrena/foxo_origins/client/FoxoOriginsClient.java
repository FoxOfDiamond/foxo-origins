package syrena.foxo_origins.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.client.network.FoxoOriginsClientboundPacketListener;
import syrena.foxo_origins.client.util.registry.FoxoOriginsKeyMappings;

public class FoxoOriginsClient implements ClientModInitializer {
    public static Logger LOGGER = LoggerFactory.getLogger(FoxoOrigins.MOD_ID + "/client");
	@Override
	public void onInitializeClient() {
        FoxoOriginsClientboundPacketListener.init();
        FoxoOriginsKeyMappings.registerAll();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}