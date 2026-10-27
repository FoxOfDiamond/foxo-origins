package syrena.foxo_origins;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.duck.internal.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.duck.internal.ServerCommandStorageHolder;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundClearCachedLogsPacket;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdatePowersPacket;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syrena.foxo_origins.command.OriginCommand;
import syrena.foxo_origins.network.packet.FoxoOriginsPackets;
import syrena.foxo_origins.network.packet.clientbound.ClientboundUpdateOriginsPacket;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin.ServerOriginManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

public class FoxoOrigins implements ModInitializer {
	public static final String MOD_ID = "foxo-origins";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    static MinecraftServer server;
    static final IntSet CACHED_LOGS = new IntOpenHashSet();


	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Foxo-Origins");

        FoxoOriginsPackets.registerAll();
        FoxoOriginsRegistry.registerAll();

        OriginManager.getInstance().init();

        CommandRegistrationCallback.EVENT.register((dispatcher, context,selection) -> {
            OriginCommand.register(dispatcher.getRoot(),context);
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> FoxoOrigins.server = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> FoxoOrigins.server = null);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
