package syrena.foxo_origins.client.hud.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import io.github.eggohito.kaya.power.PowerHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin_layer.OriginLayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class FoxoOriginsListEntry extends AbstractSelectionList.Entry<FoxoOriginsListEntry> {
    private final FormattedCharSequence INDENT = FormattedCharSequence.forward(" ".repeat(FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_LINE_INDENT), Style.EMPTY);

    public Font font;
    public Component title;
    public Component shortDescription;
    public Component description;
    private int cachedWidth = 0;
    private FormattedCharSequence truncatedTitle;
    private FormattedCharSequence truncatedShortDescription;
    private List<FormattedCharSequence> wrappedDescription = new ArrayList<>();
    private List<Boolean> descriptionLinebreaks = new ArrayList<>();
    private int descriptionLinebreakCount = 0;
    public Either<ResourceLocation, ItemStack> icon;
    public int impact;
    public int order;
    public boolean expanded = false;
    public boolean hovered = false;

    public boolean hasDescription(){
        return !Objects.equals(description.toString(), "empty");
    }
    public FoxoOriginsListEntry(Font font, Component title, Component shortDescription, Component description, Either<ResourceLocation, ItemStack> icon, int impact, int order){
        this.title = title;
        this.font = font;
        this.shortDescription = MutableComponent.create(shortDescription.getContents()).setStyle(Style.EMPTY.withItalic(true));
        this.description = description;
        this.order = order;
        this.impact = impact;
        this.icon = icon;
    }
    public static FoxoOriginsListEntry newEntry(OriginLayer layer, boolean allowRandom){
        return new FoxoOriginsOriginLayerListEntry(layer, allowRandom, Minecraft.getInstance().font,layer.name(),layer.shortDescription(),layer.description(), layer.icon(),layer.impact(),layer.order());
    }
    public static FoxoOriginsListEntry newEntry(Origin origin, boolean choosable, boolean allowRandom){
        return new FoxoOriginsOriginListEntry(origin, choosable, allowRandom,Minecraft.getInstance().font,origin.name(), origin.shortDescription(), origin.description(), origin.icon(),origin.impact(),origin.order());
    }
    public static FoxoOriginsListEntry newEntry(PowerHolder<?> power){
        return new FoxoOriginsListEntry(Minecraft.getInstance().font,power.name(), power.description(), power.description(), null,0,0);
    }
    public int getDescriptionHeight(){
        return expanded ? wrappedDescription.size() * (font.lineHeight + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_LINE_SPACING) + (descriptionLinebreakCount) * FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_PARAGRAPH_SPACING + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.y:0;
    }
    public void render(GuiGraphics guiGraphics, boolean isLast, int rowTop, int rowLeft, int rowWidth, boolean hovered) {
        int itemHeight = FoxoOriginsDisplayScreen.ENTRY_HEIGHT;
        this.hovered = hovered;
        if (this.cachedWidth != rowWidth){
            this.cachedWidth = rowWidth;
            truncatedTitle = truncateText(title,rowWidth-itemHeight);
            truncatedShortDescription = truncateText(shortDescription,rowWidth-itemHeight);
            wrappedDescription = wrapText(description,cachedWidth - FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.x * 2);
        }
        //FoxoOrigins.LOGGER.info(String.format("%s, %s, %s, %s, %s, %s, %s, %s",id,rowTop,rowLeft,rowWidth,itemHeight,mouseX,mouseY,hovered));
        guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.LIST_ENTRY_LOCATION,rowLeft,rowTop,rowWidth,itemHeight);
        if (hovered){
            guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.WINDOW_BACKGROUND_LOCATION,rowLeft,rowTop,rowWidth,itemHeight, new Color(1,1,1,0.5f).getRGB());
        }

        if (icon != null) {
            icon.ifLeft(spriteLocation -> {
                guiGraphics.blitSprite(RenderType::guiTextured, spriteLocation, rowLeft, rowTop, itemHeight, itemHeight);
            });
            icon.ifRight(itemStack -> {
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.scale(2, 2, 2);
                pose.translate((float) (-rowLeft) / 2, (float) (-rowTop) / 2, (float) (-0) / 2);
                guiGraphics.renderFakeItem(itemStack, rowLeft, rowTop, 1);
                pose.popPose();
            });
        }


        guiGraphics.drawString(font,truncatedTitle,rowLeft+itemHeight,rowTop + FoxoOriginsDisplayScreen.IMPACT_SIZE.y, Color.white.getRGB(),false);
        guiGraphics.drawString(font,truncatedShortDescription,rowLeft+itemHeight,rowTop + font.lineHeight + FoxoOriginsDisplayScreen.IMPACT_SIZE.y, Color.darkGray.getRGB(),false);


        guiGraphics.blitSprite(RenderType::guiTextured,FoxoOriginsDisplayScreen.getImpactPath(impact),rowLeft+itemHeight,rowTop,FoxoOriginsDisplayScreen.IMPACT_SIZE.x,FoxoOriginsDisplayScreen.IMPACT_SIZE.y);

        if (!isLast){
            guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.LIST_SEPARATOR_LOCATION,rowLeft,rowTop + itemHeight + getDescriptionHeight(),rowWidth,FoxoOriginsDisplayScreen.LIST_SEPARATOR_HEIGHT);
        }
        if(expanded){
            guiGraphics.blitSprite(RenderType::guiTextured,FoxoOriginsDisplayScreen.LIST_EXPANDED_ENTRY_LOCATION,rowLeft,rowTop + itemHeight, rowWidth, getDescriptionHeight());
            int descriptionHeight = rowTop + itemHeight + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.y;
            int i = 0;
            for (FormattedCharSequence sequence : wrappedDescription) {
                guiGraphics.drawString(font, sequence, rowLeft + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.x, descriptionHeight, Color.white.getRGB(), false);
                descriptionHeight += font.lineHeight + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_LINE_SPACING;
                if (i < (descriptionLinebreaks.size()-1) && descriptionLinebreaks.get(i + 1)){
                    descriptionHeight += FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_PARAGRAPH_SPACING;
                }
                i += 1;
            }
        }
    }


    private static String stripColor(String string) {
        return Minecraft.getInstance().options.chatColors().get() ? string : ChatFormatting.stripFormatting(string);
    }
    public List<FormattedCharSequence> wrapText(FormattedText formattedText, int width) {
        ComponentCollector componentCollector = new ComponentCollector();
        formattedText.visit((style, string) -> {
            componentCollector.append(FormattedText.of(stripColor(string), style));
            return Optional.empty();
        }, Style.EMPTY);
        List<FormattedCharSequence> list = Lists.newArrayList();
        List<Boolean> linebreakList = Lists.newArrayList();
        this.descriptionLinebreakCount = 0;
        font.getSplitter().splitLines(componentCollector.getResultOrEmpty(), width, Style.EMPTY, (formattedTextx, boolean_) -> {
            FormattedCharSequence formattedCharSequence = Language.getInstance().getVisualOrder(formattedTextx);
            list.add(!boolean_ ? FormattedCharSequence.composite(INDENT, formattedCharSequence) : formattedCharSequence);
            linebreakList.add(!boolean_);
            if (!boolean_){
                this.descriptionLinebreakCount += 1;
            }
        });
        this.descriptionLinebreaks = linebreakList;
        return list.isEmpty() ? Lists.newArrayList(FormattedCharSequence.EMPTY) : list;
    }
    public FormattedCharSequence truncateText(Component text, int width){
        FormattedText clippedText = font.substrByWidth(text, width - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(clippedText, CommonComponents.ELLIPSIS));
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        expanded = hasDescription() && !expanded;
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {}
}
