package syrena.foxo_origins.client.screen;

import io.github.eggohito.kaya.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsListEntry;
import syrena.foxo_origins.client.util.MiscUtilClient;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.List;
import java.util.Map;

public class OriginLayersSelectScreen extends FoxoOriginsDisplayScreen{
    List<OriginLayer> layers;
    public OriginLayersSelectScreen(Component component, List<OriginLayer> layers) {
        this.layers = layers;
        super(component);
    }

    @Override
    public void onClose() {
        Player player = Minecraft.getInstance().player;
        Map<OriginLayer, Origin> attachment = player.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT);
        if (!MiscUtilClient.checkIfSelectionFinished(attachment)) {
            Minecraft.getInstance().setScreen(new OriginLayersSelectScreen(Component.translatable("gui.foxo-origins.view_layer.title"), new ObjectArrayList<>(OriginLayerManager.getInstance().values().iterator())));
            return;
        }
        super.onClose();
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, Component.translatable("gui.foxo-origins.choose_origin.empty_list.layer"), LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        layers.forEach(layer -> {
            assert minecraft != null;
            assert minecraft.player != null;
            Context thisContext = MiscUtilClient.thisContext(minecraft);
            if (!layer.isVisible(thisContext)) return;
            Map<OriginLayer,Origin> attachment = minecraft  .player.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT);
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(layer, MiscUtilClient.layerIsPlaceholder(attachment,layer),layer.allowsRandom(thisContext)));
        });

        super.init();
    }
}
