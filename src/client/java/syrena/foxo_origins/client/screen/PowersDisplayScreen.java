package syrena.foxo_origins.client.screen;

import com.mojang.datafixers.util.Either;
import io.github.eggohito.kaya.power.PowerHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.list.FoxoOriginsListEntry;
import syrena.foxo_origins.client.util.MiscUtilClient;

import java.util.List;

public class PowersDisplayScreen extends FoxoOriginsDisplayScreen{
    List<PowerHolder<?>> powers;
    public PowersDisplayScreen(Component component, Either<ResourceLocation, ItemStack> icon, List<PowerHolder<?>> powers) {
        this.powers = powers;
        super(component, icon);
    }

    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, this, Component.translatable("gui.foxo-origins.choose_origin.empty_list.power"), LIST_SIZE.x, LIST_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        this.layout.addAnchoredChild(Button.builder(CommonComponents.GUI_DONE, _ -> {
            this.onClose();
        }).width(100).build(),WINDOW_SIZE.x/2-55,WINDOW_SIZE.y + 6,0.5f,0);

        this.layout.addAnchoredChild(Button.builder(Component.translatable("gui.foxo-origins.choose_origin.select_origin"), _ -> {
            this.onClose();
        }).width(100).build(),WINDOW_SIZE.x/2+55,WINDOW_SIZE.y + 6,0.5f,0);

        powers.forEach(power -> {
            assert minecraft != null;
            if (power.isHidden(MiscUtilClient.thisContext(minecraft))) return;
            list.addEntryOrdered(FoxoOriginsListEntry.newEntry(power));
        });

        super.init();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.lastScreen);
    }
}
