package syrena.foxo_origins.client.key.handler;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;
import syrena.foxo_origins.client.screen.OriginLayersSelectScreen;
import syrena.foxo_origins.origin_layer.OriginLayerManager;

public class FoxoOriginsKeyMappingHandler {
    public static void registerHandlers(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (FoxoOriginsKeyMapping.VIEW_ORIGINS_KEYMAPPING.consumeClick()){
                if (!(client.screen instanceof FoxoOriginsDisplayScreen)){
                    client.setScreen(new OriginLayersSelectScreen(Component.translatable("gui.foxo-origins.view_layer.title"), new ObjectArrayList<>(OriginLayerManager.getInstance().values().iterator())));
                } else {
                    client.screen.onClose();
                }
            }
        });
    }
}
