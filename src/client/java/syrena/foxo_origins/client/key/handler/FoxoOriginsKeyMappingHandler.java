package syrena.foxo_origins.client.key.handler;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;

public class FoxoOriginsKeyMappingHandler {
    public static void registerHandlers(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (FoxoOriginsKeyMapping.VIEW_ORIGINS_KEYMAPPING.consumeClick()){
                if (!(client.screen instanceof FoxoOriginsDisplayScreen)){
                    client.setScreen(new FoxoOriginsDisplayScreen(Component.translatable("foxo-origins.gui.view_origin.title")));
                } else {
                    client.screen.onClose();
                }
            }
        });
    }
}
