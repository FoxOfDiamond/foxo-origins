package syrena.foxo_origins.client.hud.widget.list;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;
import syrena.foxo_origins.client.screen.PowersDisplayScreen;
import syrena.foxo_origins.network.packet.serverbound.ServerboundSelectOriginPacket;
import syrena.foxo_origins.origin.OriginHolder;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin_layer.OriginLayerHolder;
import syrena.foxo_origins.origin_layer.OriginLayerManager;

import java.util.Optional;

public class FoxoOriginsOriginSelectionListEntry extends FoxoOriginsListEntry{
    SpriteIconButton viewButton;
    SpriteIconButton selectButton;
    int BUTTON_SIZE = FoxoOriginsDisplayScreen.ENTRY_ICON_BUTTON_SIZE;
    boolean choosable;
    boolean allowRandom;
    public FoxoOriginsOriginSelectionListEntry(OriginHolder origin, OriginLayerHolder layer, boolean choosable, boolean allowRandom, Font font, Component title, Component shortDescription, Component description, Either<ResourceLocation, ItemStack> icon, int impact, int order) {
        super(font, title, shortDescription, description, icon, impact, order);
        this.choosable = choosable;
        this.allowRandom = allowRandom;
        this.viewButton = new SpriteIconButton.Builder(Component.empty(),(_ -> {
            Minecraft.getInstance().setScreen(new PowersDisplayScreen(this.title, icon, origin.origin().powers().elements()));
        }),true).size(BUTTON_SIZE,BUTTON_SIZE).sprite(FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_LOCATION,BUTTON_SIZE,BUTTON_SIZE).build();
        if (this.choosable){
            this.selectButton = new SpriteIconButton.Builder(Component.empty(),(onPress -> {
                ClientPlayNetworking.send(new ServerboundSelectOriginPacket(OriginLayerManager.getInstance().getKey(layer), Optional.of(OriginManager.getInstance().getKey(origin))));
            }),true).size(BUTTON_SIZE,BUTTON_SIZE).sprite(FoxoOriginsDisplayScreen.ENTRY_SELECT_BUTTON_LOCATION,BUTTON_SIZE,BUTTON_SIZE).build();
        }
    }

    @Override
    protected void cacheProcessedComponents(int iconOffset){
        int rowHeight = FoxoOriginsDisplayScreen.ENTRY_HEIGHT;
        int spacing = (rowHeight - BUTTON_SIZE) /2;
        truncatedTitle = truncateText(title,cachedWidth - iconOffset - rowHeight + spacing * 2);
        truncatedShortDescription = truncateText(shortDescription,cachedWidth - iconOffset - rowHeight * 2);
        wrappedDescription = wrapText(description,cachedWidth - FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.x * 2);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isLast, int rowTop, int rowLeft, int rowWidth, boolean hovered) {
        super.render(guiGraphics, mouseX, mouseY, isLast,rowTop,rowLeft,rowWidth,hovered);
        int rowHeight = FoxoOriginsDisplayScreen.ENTRY_HEIGHT;
        int spacing = (rowHeight - BUTTON_SIZE) /2;
        this.viewButton.setPosition(rowLeft + rowWidth - rowHeight + spacing,rowTop + spacing);
        this.viewButton.render(guiGraphics,mouseX, mouseY,1);
        if (this.choosable){
            this.selectButton.setPosition(rowLeft + rowWidth - rowHeight - BUTTON_SIZE,rowTop + spacing);
            this.selectButton.render(guiGraphics,mouseX,mouseY,1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (viewButton != null && viewButton.mouseClicked(mouseX,mouseY,mouseButton)) return false;
        if (selectButton != null && selectButton.mouseClicked(mouseX,mouseY,mouseButton)) return false;
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
