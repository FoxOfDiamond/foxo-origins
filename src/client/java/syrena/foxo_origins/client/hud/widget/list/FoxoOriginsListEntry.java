package syrena.foxo_origins.client.hud.widget.list;

import com.google.common.collect.Lists;
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
import syrena.foxo_origins.client.util.MiscUtilClient;
import syrena.foxo_origins.origin.OriginHolder;
import syrena.foxo_origins.origin_layer.OriginLayerHolder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class FoxoOriginsListEntry extends AbstractSelectionList.Entry<FoxoOriginsListEntry> {
    private final FormattedCharSequence INDENT = FormattedCharSequence.forward(" ".repeat(FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_LINE_INDENT), Style.EMPTY);

    protected Font font;
    protected Component title;
    protected Component shortDescription;
    protected Component description;
    protected int cachedWidth = 0;
    protected FormattedCharSequence truncatedTitle;
    protected FormattedCharSequence truncatedShortDescription;
    protected List<FormattedCharSequence> wrappedDescription = new ArrayList<>();
    protected List<Boolean> descriptionLinebreaks = new ArrayList<>();
    protected int descriptionLinebreakCount = 0;
    protected final Either<ResourceLocation, ItemStack> icon;
    protected int impact;
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
    public static FoxoOriginsListEntry newEntry(OriginLayerHolder layer, boolean requiresAction, boolean allowRandom){
        return new FoxoOriginsOriginLayerSelectionListEntry(layer, requiresAction, allowRandom, Minecraft.getInstance().font,layer.layer().name(),layer.layer().shortDescription(),layer.layer().description(), layer.layer().icon().orElse(null),layer.layer().impact(),layer.layer().order());
    }
    public static FoxoOriginsListEntry newEntry(OriginHolder origin, OriginLayerHolder layer, boolean choosable, boolean allowRandom){
        return new FoxoOriginsOriginSelectionListEntry(origin, layer, choosable, allowRandom,Minecraft.getInstance().font,origin.origin().name(), origin.origin().shortDescription(), origin.origin().description(), origin.origin().icon().orElse(null),origin.origin().impact(),origin.origin().order());
    }
    public static FoxoOriginsListEntry newEntry(PowerHolder<?> power){
        return new FoxoOriginsListEntry(Minecraft.getInstance().font,power.name(), power.description(), power.description(), null,-1,0);
    }
    public int getDescriptionHeight(){
        return expanded ? wrappedDescription.size() * (font.lineHeight + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_LINE_SPACING) + (descriptionLinebreakCount - 1) * FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_PARAGRAPH_SPACING + FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.y:0;
    }
    protected void cacheProcessedComponents(int iconOffset){
        truncatedTitle = truncateText(title,cachedWidth - iconOffset);
        truncatedShortDescription = truncateText(shortDescription,cachedWidth - iconOffset);
        wrappedDescription = wrapText(description,cachedWidth - FoxoOriginsDisplayScreen.ENTRY_DESCRIPTION_MARGINS.x * 2);
    }
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isLast, int rowTop, int rowLeft, int rowWidth, boolean hovered) {
        int itemHeight = FoxoOriginsDisplayScreen.ENTRY_HEIGHT;
        int iconOffset = (icon == null ? 0 : itemHeight) + 4;

        this.hovered = hovered;
        if (this.cachedWidth != rowWidth){
            this.cachedWidth = rowWidth;
            cacheProcessedComponents(iconOffset);
        }
        //FoxoOrigins.LOGGER.info(String.format("%s, %s, %s, %s, %s, %s, %s, %s",id,rowTop,rowLeft,rowWidth,itemHeight,mouseX,mouseY,hovered));
        guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.LIST_ENTRY_LOCATION,rowLeft,rowTop,rowWidth,itemHeight);
        if (hovered){
            guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.WINDOW_BACKGROUND_LOCATION,rowLeft,rowTop,rowWidth,itemHeight, new Color(1,1,1,0.5f).getRGB());
        }

        if (icon != null) {
            int iconSize = 24;
            int offset = Math.round((itemHeight - iconSize) / 2f);
            icon.ifLeft(spriteLocation -> {
                guiGraphics.blitSprite(RenderType::guiTextured, spriteLocation, rowLeft + offset, rowTop + offset, iconSize, iconSize);
            });
            icon.ifRight(itemStack -> {
                MiscUtilClient.renderScaledFakeItem(guiGraphics,itemStack,rowLeft + offset,rowTop + offset,iconSize);
            });
        }
        if (0<=impact && impact <= 5){
            guiGraphics.blitSprite(RenderType::guiTextured,FoxoOriginsDisplayScreen.getImpactPath(impact),rowLeft + iconOffset,rowTop + 2,FoxoOriginsDisplayScreen.IMPACT_SIZE.x,FoxoOriginsDisplayScreen.IMPACT_SIZE.y);
            guiGraphics.drawString(font,truncatedTitle,rowLeft + iconOffset,rowTop + FoxoOriginsDisplayScreen.IMPACT_SIZE.y + 4, Color.white.getRGB(),false);
            guiGraphics.drawString(font,truncatedShortDescription,rowLeft + iconOffset,rowTop + font.lineHeight + FoxoOriginsDisplayScreen.IMPACT_SIZE.y + 5, Color.darkGray.getRGB(),false);
        } else {
            guiGraphics.drawString(font,truncatedTitle,rowLeft + iconOffset,rowTop + 4, Color.white.getRGB(),false);
            guiGraphics.drawString(font,truncatedShortDescription,rowLeft + iconOffset,rowTop + font.lineHeight + 5, Color.darkGray.getRGB(),false);
        }


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
        return Language.getInstance().getVisualOrder(FormattedText.composite(clippedText, clippedText.getString().equals(text.getString())?Component.empty():CommonComponents.ELLIPSIS));
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        expanded = hasDescription() && !expanded;
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {}
}
