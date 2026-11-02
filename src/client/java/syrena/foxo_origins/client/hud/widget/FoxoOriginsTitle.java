package syrena.foxo_origins.client.hud.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;
import syrena.foxo_origins.client.util.MiscUtilClient;

import java.awt.*;

public class FoxoOriginsTitle extends AbstractWidget {
    Component title;
    Either<ResourceLocation, ItemStack> icon;
    public FoxoOriginsTitle(Component title, Either<ResourceLocation, ItemStack> icon) {
        super(0,0, FoxoOriginsDisplayScreen.TITLE_SIZE.x,FoxoOriginsDisplayScreen.TITLE_SIZE.y, title);
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        int iconOffset = icon == null ? 0 : height;
        Font font = Minecraft.getInstance().font;
        float textScale = 1.5f;
        int iconSize = Math.round(height * textScale/2);
        int centeredOffset = width/2 - Math.round(font.width(this.title)/2f * textScale);

        if (icon != null) {
            int offset = Math.round((height - iconSize) / 2f);
            icon.ifLeft(spriteLocation -> {
                guiGraphics.blitSprite(RenderType::guiTextured, spriteLocation, getX() + offset + centeredOffset - height, getY() + offset, iconSize, iconSize);
            });
            icon.ifRight(itemStack -> {
                MiscUtilClient.renderScaledFakeItem(guiGraphics,itemStack,getX() + offset + centeredOffset - height,getY() + offset - 4,iconSize);
            });
        }
        int textOffset = (int) ((height - font.lineHeight * textScale) / 2);
        int x = getX() + textOffset + centeredOffset;
        int y = getY() + textOffset;
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(textScale,textScale,textScale);
        pose.translate(x / textScale,y / textScale,0);
        guiGraphics.drawString(font, this.title, 0, 0, Color.white.getRGB(), true);
        pose.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
