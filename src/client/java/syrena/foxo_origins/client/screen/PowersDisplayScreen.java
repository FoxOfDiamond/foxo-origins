package syrena.foxo_origins.client.screen;

import com.mojang.datafixers.util.Either;
import io.github.eggohito.kaya.context.Context;
import io.github.eggohito.kaya.power.PowerHolder;
import io.github.eggohito.kaya.registry.context.KayaContextParameters;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsListEntry;

import java.util.List;

public class PowersDisplayScreen extends FoxoOriginsDisplayScreen{
    List<PowerHolder<?>> powers;
    public PowersDisplayScreen(Component component, Either<ResourceLocation, ItemStack> icon, List<PowerHolder<?>> powers) {
        this.powers = powers;
        super(component, icon);
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        powers.forEach(power -> {
            assert minecraft != null;
            Context thisContext = new Context.Builder().withRequired(KayaContextParameters.THIS_ENTITY,minecraft.player).build(minecraft.level);
            if (power.isHidden(thisContext)) return;
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(power));
        });

        super.init();
    }
}
