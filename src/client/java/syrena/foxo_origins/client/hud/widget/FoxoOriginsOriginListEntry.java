package syrena.foxo_origins.client.hud.widget;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;
import syrena.foxo_origins.client.screen.PowersDisplayScreen;
import syrena.foxo_origins.origin.Origin;

public class FoxoOriginsOriginListEntry extends FoxoOriginsListEntry{
    SpriteIconButton viewButton;
    boolean choosable;
    boolean allowRandom;
    public FoxoOriginsOriginListEntry(Origin origin, boolean choosable, boolean allowRandom, Font font, Component title, Component shortDescription, Component description, Either<ResourceLocation, ItemStack> icon, int impact, int order) {
        super(font, title, shortDescription, description, icon, impact, order);
        this.choosable = choosable;
        this.allowRandom = allowRandom;
        this.viewButton = new SpriteIconButton.Builder(Component.empty(),(onPress -> {
            Minecraft.getInstance().setScreen(new PowersDisplayScreen(this.title, origin.powers().elements()));
        }),true).size(FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_SIZE.x,FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_SIZE.y).sprite(FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_LOCATION,FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_SIZE.x,FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_SIZE.y).build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean isLast, int rowTop, int rowLeft, int rowWidth, boolean hovered) {
        super.render(guiGraphics, isLast,rowTop,rowLeft,rowWidth,hovered);
        this.viewButton.setPosition(rowLeft + FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_OFFSET.x,rowTop + FoxoOriginsDisplayScreen.ENTRY_VIEW_BUTTON_OFFSET.y);
        this.viewButton.render(guiGraphics,0,0,1);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (viewButton != null && viewButton.mouseClicked(mouseX,mouseY,mouseButton)) return false;
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseMoved(double d, double e) {
        if (viewButton != null) viewButton.mouseMoved(d,e);
        super.mouseMoved(d, e);
    }
}
