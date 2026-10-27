package syrena.foxo_origins.client.util.registry;

import syrena.foxo_origins.client.key.handler.FoxoOriginsKeyMapping;
import syrena.foxo_origins.client.key.handler.FoxoOriginsKeyMappingHandler;

public class FoxoOriginsKeyMappings {
    public static void registerAll(){
        FoxoOriginsKeyMapping.registerKeyMappings();
        FoxoOriginsKeyMappingHandler.registerHandlers();
    }
}
