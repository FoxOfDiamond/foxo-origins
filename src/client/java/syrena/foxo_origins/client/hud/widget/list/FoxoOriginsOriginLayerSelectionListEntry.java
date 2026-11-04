package syrena.foxo_origins.client.hud.widget.list;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;
import syrena.foxo_origins.client.screen.OriginsSelectScreen;
import syrena.foxo_origins.client.util.MiscUtilClient;
import syrena.foxo_origins.network.packet.serverbound.ServerboundSelectOriginPacket;
import syrena.foxo_origins.origin_layer.OriginLayerHolder;
import syrena.foxo_origins.origin_layer.OriginLayerManager;

import java.util.Optional;

public class FoxoOriginsOriginLayerSelectionListEntry extends FoxoOriginsListEntry{
    SpriteIconButton viewButton;
    SpriteIconButton randomizeButton;
    ImageWidget requiresActionIcon;
    int BUTTON_SIZE = FoxoOriginsDisplayScreen.ENTRY_ICON_BUTTON_SIZE;
    boolean allowRandom;
    boolean requiresAction;
    public FoxoOriginsOriginLayerSelectionListEntry(OriginLayerHolder layer, boolean requiresAction, boolean allowRandom, Font font, Component title, Component shortDescription, Component description, Either<ResourceLocation, ItemStack> icon, int impact, int order) {
        super(font, title, shortDescription, description, icon, impact, order);
        this.allowRandom = allowRandom;
        this.requiresAction = requiresAction;
        this.viewButton = new SpriteIconButton.Builder(Component.empty(),(onPress -> {
            Minecraft.getInstance().setScreen(new OriginsSelectScreen(this.title, icon, layer));
        }),true).size(BUTTON_SIZE,BUTTON_SIZE).sprite(FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_LOCATION,BUTTON_SIZE,BUTTON_SIZE).build();
        if (this.requiresAction){
            this.requiresActionIcon = ImageWidget.sprite(FoxoOriginsDisplayScreen.ENTRY_REQUIRES_ACTION_ICON_SIZE,FoxoOriginsDisplayScreen.ENTRY_REQUIRES_ACTION_ICON_SIZE,FoxoOriginsDisplayScreen.ENTRY_REQUIRES_ACTION_ICON_LOCATION);
        }
        if (this.allowRandom){
            this.randomizeButton = new SpriteIconButton.Builder(Component.empty(),(onPress -> {
                ClientPlayNetworking.send(new ServerboundSelectOriginPacket(OriginLayerManager.getInstance().getKey(layer), Optional.empty()));
            }),true).size(BUTTON_SIZE,BUTTON_SIZE).sprite(FoxoOriginsDisplayScreen.ENTRY_RANDOMIZE_BUTTON_LOCATION,BUTTON_SIZE,BUTTON_SIZE).build();
            MutableComponent tooltip = Component.translatable("gui.foxo-origins.choose_origin.randomize_layer.tooltip");
            layer.layer().origins().elements().forEach(origin -> {
                if (origin.origin().allowsRandom(MiscUtilClient.thisContext(Minecraft.getInstance()))){
                    tooltip.append(CommonComponents.NEW_LINE);
                    tooltip.append(origin.origin().name());
                }
            });
            this.randomizeButton.setTooltip(Tooltip.create(tooltip));
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
        super.render(guiGraphics, mouseX, mouseY, isLast, rowTop,rowLeft,rowWidth,hovered);
        int rowHeight = FoxoOriginsDisplayScreen.ENTRY_HEIGHT;
        int spacing = (rowHeight - BUTTON_SIZE) /2;
        this.viewButton.setPosition(rowLeft + rowWidth - rowHeight + spacing,rowTop + spacing);
        this.viewButton.render(guiGraphics,mouseX, mouseY,1);
        if (this.allowRandom) {
            this.randomizeButton.setPosition(rowLeft + rowWidth - rowHeight - BUTTON_SIZE, rowTop + spacing);
            this.randomizeButton.render(guiGraphics, mouseX, mouseY, 1);
        }
        if (this.requiresAction){
            int iconSize = FoxoOriginsDisplayScreen.ENTRY_REQUIRES_ACTION_ICON_SIZE;
            this.requiresActionIcon.setPosition(rowLeft + rowWidth - spacing - iconSize/2, rowTop + spacing - iconSize/2);
            this.requiresActionIcon.render(guiGraphics, mouseX, mouseY, 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (viewButton != null && viewButton.mouseClicked(mouseX,mouseY,mouseButton)) return false;
        if (randomizeButton != null && randomizeButton.mouseClicked(mouseX,mouseY,mouseButton)) return false;
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
