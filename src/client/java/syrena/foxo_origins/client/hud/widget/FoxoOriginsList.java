package syrena.foxo_origins.client.hud.widget;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector2i;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;

import java.util.List;
import java.util.Objects;

public class FoxoOriginsList extends AbstractSelectionList<FoxoOriginsListEntry> implements ContainerEventHandler {
    protected List<FoxoOriginsListEntry> children;
    private double lastY = 0;

    public FoxoOriginsList(Minecraft minecraft, Screen screen, int w, int h) {
        super(minecraft, w, h,0, 0);
        this.headerHeight = 0;
        ScreenMouseEvents.beforeMouseClick(screen).register((screen1, mouseX, mouseY, mouseButton) -> {
            this.updateScrolling(mouseX, mouseY, mouseButton);
        });
        ScreenMouseEvents.afterMouseRelease(screen).register((screen1, mouseX, mouseY, mouseButton) -> {
            this.scrolling = false;
        });
        ScreenEvents.afterTick(screen).register((screen1) -> {
            double mouseY = minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
            double dY = mouseY - this.lastY;
            this.lastY = mouseY;

            if (!scrolling) return;

            if (mouseY < this.getY()) {
                this.setScrollAmount(0.0);
            } else if (mouseY > this.getBottom()) {
                this.setScrollAmount(this.maxScrollAmount());
            } else {
                double clampedScroll = Math.max(1, this.maxScrollAmount());
                double scaledScroll = Math.max(1.0, clampedScroll / (scrollBarHeight() - this.scrollerHeight()));
                this.setScrollAmount(this.scrollAmount() + dY * scaledScroll);
            }

        });

    }

    public void addEntryOrdered(FoxoOriginsListEntry entry){
        for (int i = 0; i < getItemCount(); i++) {
            if (this.children().get(i).order > entry.order){
                this.children().add(i,entry);
                return;
            }
        }
        this.children().add(entry);
    }
    @Override
    protected int contentHeight() {
        return getHeightToIndex(this.getItemCount());
    }

    private int getHeightToIndex(int index){
        int totalHeight = 0;
        for (int i = 0; i < Math.min(getItemCount(),index); i++) {
            FoxoOriginsListEntry entry = this.getEntry(i);
            totalHeight += FoxoOriginsDisplayScreen.ENTRY_HEIGHT + FoxoOriginsDisplayScreen.LIST_SEPARATOR_HEIGHT + entry.getDescriptionHeight();
        }
        return Math.max(totalHeight,0);
    }
    @Override
    protected double scrollRate() {
        return 10;
    }
    public final FoxoOriginsListEntry entryAtPosition(double mouseX, double mouseY) {
        if (mouseX < getX() || mouseX > getX() + getWidth()) return null;
        int currentY = getY()-(int)(scrollAmount());
        for (int i = 0; i < getItemCount(); i++) {
            FoxoOriginsListEntry entry = this.getEntry(i);
            currentY += FoxoOriginsDisplayScreen.ENTRY_HEIGHT;
            if (currentY > mouseY){
                return this.getEntry(i);
            }
            currentY += FoxoOriginsDisplayScreen.LIST_SEPARATOR_HEIGHT + entry.getDescriptionHeight();
            if (currentY > mouseY) break;
        }
        return null;
    }
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.WINDOW_BACKGROUND_LOCATION,getX(),getY(),getWidth(),getHeight());

        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.entryAtPosition(mouseX, mouseY) : null;
        //this.renderListBackground(guiGraphics);

        this.enableScissor(guiGraphics);
        this.renderListItems(guiGraphics, mouseX, mouseY, a);
        guiGraphics.disableScissor();

        this.renderScrollbar(guiGraphics);
    }
    @Override
    public int getRowTop(int i) {
        return this.getY() - (int)this.scrollAmount() + getHeightToIndex(i);
    }
    @Override
    public int getRowBottom(int i) {
        return this.getRowTop(i) + FoxoOriginsDisplayScreen.ENTRY_HEIGHT + + FoxoOriginsDisplayScreen.LIST_SEPARATOR_HEIGHT + getEntry(i).getDescriptionHeight();
    }
    @Override
    protected void renderListItems(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        int rowLeft = this.getX();
        int rowWidth = this.getWidth();
        int itemCount = this.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            int rowTop = this.getRowTop(i);
            int rowBottom = this.getRowBottom(i);
            if (rowBottom >= this.getY() && rowTop <= this.getBottom()) {
                this.getEntry(i).render(guiGraphics, i == (itemCount-1), rowTop, rowLeft, rowWidth, Objects.equals(this.hovered, this.getEntry(i)));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        FoxoOriginsListEntry entry = entryAtPosition(mouseX,mouseY);
        if (mouseButton == 0 && entry != null){
            this.setFocused(entry);
            this.setDragging(true);
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isDragging()){
            this.setDragging(false);
            if (this.getFocused() != null && this.getFocused() == entryAtPosition(mouseX,mouseY)){
                this.getFocused().mouseReleased(mouseX,mouseY,mouseButton);
                return true;
            }
            return false;
        }
        return false;
    }
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.scrolling) {
            return true;
        } else {
            return super.mouseDragged(d, e, i, f, g);
        }
    }
    protected Vector2i scrollbarOffset(){
        return FoxoOriginsDisplayScreen.LIST_SCROLLBAR_OFFSET;
    }
    protected Vector2i scrollerSize(){
        return FoxoOriginsDisplayScreen.SCROLLBAR_SCROLLER_SIZE;
    }
    protected int scrollBarHeight(){ return FoxoOriginsDisplayScreen.SCROLLBAR_SIZE.y;}
    @Override
    protected int scrollBarY() {
        return Math.max(this.getY() + scrollbarOffset().y, (int) this.scrollAmount() * (this.scrollBarHeight() - this.scrollerHeight()) / this.maxScrollAmount() + this.getY() + scrollbarOffset().y);
    }
    @Override
    public boolean updateScrolling(double mouseX, double mouseY, int button) {
        if (this.scrollbarVisible()
                && this.isValidClickButton(button)
                && mouseX >= this.scrollBarX()
                && mouseX <= this.scrollBarX() + scrollerSize().x
                && mouseY >= this.getY() + scrollbarOffset().y
                && mouseY < this.getY() + scrollbarOffset().y + scrollBarHeight())
        {
            this.scrolling = true;
            if (mouseY < this.scrollBarY()
                || mouseY >= this.scrollBarY() + scrollerSize().y()) {
                setScrollAmount((mouseY-this.getY() - scrollbarOffset().y- (double) scrollerHeight() /2)/(scrollBarHeight()-scrollerHeight())*maxScrollAmount());
            }
        } else {
            this.scrolling = false;
        }
        return this.scrolling;
    }
    @Override
    protected int scrollBarX() {
        return this.getX() + scrollbarOffset().x;
    }

    @Override
    protected void renderScrollbar(GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.SCROLLBAR_LOCATION, this.getX() + scrollbarOffset().x, this.getY() + scrollbarOffset().y,FoxoOriginsDisplayScreen.SCROLLBAR_SIZE.x,FoxoOriginsDisplayScreen.SCROLLBAR_SIZE.y);
            guiGraphics.blitSprite(RenderType::guiTextured, scrolling?FoxoOriginsDisplayScreen.SCROLLBAR_SCROLLER_PRESSED_LOCATION :FoxoOriginsDisplayScreen.SCROLLBAR_SCROLLER_LOCATION, this.getX() + scrollbarOffset().x, scrollBarY(), scrollerSize().x,scrollerSize().y);
        }
    }

    @Override
    protected int scrollerHeight(){
        return scrollerSize().y;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
