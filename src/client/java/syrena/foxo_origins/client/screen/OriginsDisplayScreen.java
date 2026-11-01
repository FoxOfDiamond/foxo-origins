package syrena.foxo_origins.client.screen;

import com.mojang.datafixers.util.Either;
import io.github.eggohito.kaya.context.Context;
import io.github.eggohito.kaya.registry.context.KayaContextParameters;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsListEntry;
import syrena.foxo_origins.origin.Origin;

import java.util.List;

public class OriginsDisplayScreen extends FoxoOriginsDisplayScreen{
    List<Origin> origins;
    public OriginsDisplayScreen(Component component, Either<ResourceLocation, ItemStack> icon, List<Origin> origins) {
        this.origins = origins;
        super(component, icon);
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        origins.forEach(origin -> {
            assert minecraft != null;
            assert minecraft.player != null;
            Context thisContext = new Context.Builder().withRequired(KayaContextParameters.THIS_ENTITY,minecraft.player).build(minecraft.level);
            if (!origin.isVisible(thisContext)) return;
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(origin,origin.isChoosable(thisContext),origin.allowsRandom(thisContext)));
        });

        super.init();
    }
}
