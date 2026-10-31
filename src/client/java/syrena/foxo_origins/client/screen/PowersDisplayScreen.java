package syrena.foxo_origins.client.screen;

import io.github.eggohito.kaya.power.PowerHolder;
import net.minecraft.network.chat.Component;
import syrena.foxo_origins.client.hud.widget.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.FoxoOriginsListEntry;

import java.util.List;

public class PowersDisplayScreen extends FoxoOriginsDisplayScreen{
    List<PowerHolder<?>> powers;
    public PowersDisplayScreen(Component component, List<PowerHolder<?>> powers) {
        this.powers = powers;
        super(component);
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        powers.forEach(power -> {
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(power));
        });

        super.init();
    }
}
