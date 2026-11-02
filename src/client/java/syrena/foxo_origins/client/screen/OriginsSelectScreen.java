package syrena.foxo_origins.client.screen;

import com.mojang.datafixers.util.Either;
import io.github.eggohito.kaya.context.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsListEntry;
import syrena.foxo_origins.client.util.MiscUtilClient;
import syrena.foxo_origins.origin_layer.OriginLayer;

public class OriginsSelectScreen extends FoxoOriginsDisplayScreen{
    OriginLayer layer;
    public OriginsSelectScreen(Component component, Either<ResourceLocation, ItemStack> icon, OriginLayer layer) {
        this.layer = layer;
        super(component, icon);
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, Component.translatable("gui.foxo-origins.choose_origin.empty_list.origin"), LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        this.layout.addAnchoredChild(Button.builder(CommonComponents.GUI_DONE, _ -> {
            this.onClose();
        }).width(100).build(),WINDOW_SIZE.x/2-55,WINDOW_SIZE.y + 6,0.5f,0);

        MutableComponent randomizeTooltip = Component.translatable("gui.foxo-origins.choose_origin.randomize_layer.tooltip");
        layer.origins().elements().forEach(origin -> {
            if (origin.allowsRandom(MiscUtilClient.thisContext(Minecraft.getInstance()))){
                randomizeTooltip.append(CommonComponents.NEW_LINE);
                randomizeTooltip.append(origin.name());
            }
        });
        this.layout.addAnchoredChild(Button.builder(Component.translatable("gui.foxo-origins.choose_origin.randomize_layer"), _ -> {
            this.onClose();
        }).width(100).tooltip(Tooltip.create(randomizeTooltip)).build(),WINDOW_SIZE.x/2+55,WINDOW_SIZE.y + 6,0.5f,0);
        layer.origins().elements().forEach(origin -> {
            assert minecraft != null;
            assert minecraft.player != null;
            Context thisContext = MiscUtilClient.thisContext(minecraft);
            if (!origin.isVisible(thisContext)) return;
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(origin, layer,origin.isChoosable(thisContext),origin.allowsRandom(thisContext)));
        });

        super.init();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.lastScreen);
    }
}
