package syrena.foxo_origins.client.screen;

import io.github.eggohito.kaya.context.Context;
import io.github.eggohito.kaya.registry.context.KayaContextParameters;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.network.chat.Component;
import syrena.foxo_origins.client.hud.widget.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.FoxoOriginsListEntry;
import syrena.foxo_origins.origin_layer.OriginLayer;

import java.util.List;

public class OriginLayersDisplayScreen extends FoxoOriginsDisplayScreen{
    List<OriginLayer> layers;
    public OriginLayersDisplayScreen(Component component,List<OriginLayer> layers) {
        this.layers = layers;
        super(component);
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        layers.forEach(layer -> {
            assert minecraft != null;
            assert minecraft.player != null;
            Context thisContext = new Context.Builder().withRequired(KayaContextParameters.THIS_ENTITY,minecraft.player).build(minecraft.level);
            if (!layer.isVisible(thisContext)) return;
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(layer,layer.allowsRandom(thisContext)));
        });

        super.init();
    }
}
