package syrena.foxo_origins.client.hud.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.RenderType;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;

import javax.swing.*;

public class FoxoOriginsListEntry extends AbstractSelectionList.Entry<FoxoOriginsListEntry> {
    @Override
    public void render(GuiGraphics guiGraphics, int id, int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float a) {
        //FoxoOrigins.LOGGER.info(String.format("%s, %s, %s, %s, %s, %s, %s, %s",id,rowTop,rowLeft,rowWidth,itemHeight,mouseX,mouseY,hovered));
        guiGraphics.blitSprite(RenderType::guiTextured,FoxoOriginsDisplayScreen.LIST_ENTRY_LOCATION,rowLeft,rowTop,rowWidth,itemHeight);
    }
}
