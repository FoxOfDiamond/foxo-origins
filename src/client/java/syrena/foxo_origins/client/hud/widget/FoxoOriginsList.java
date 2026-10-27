package syrena.foxo_origins.client.hud.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector2i;
import syrena.foxo_origins.client.screen.FoxoOriginsDisplayScreen;

import java.util.List;

public class FoxoOriginsList extends AbstractSelectionList<FoxoOriginsListEntry> implements ContainerEventHandler {
    protected List<FoxoOriginsListEntry> children;

    public FoxoOriginsList(Minecraft minecraft, int w, int h, int itemHeight) {
        super(minecraft, w, h,0, itemHeight);
        this.headerHeight = 0;
    }

    @Override
    protected int contentHeight() {
        return getItemCount()*itemHeight;
    }

    @Override
    protected double scrollRate() {
        return 10;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        guiGraphics.blitSprite(RenderType::guiTextured, FoxoOriginsDisplayScreen.LIST_ENTRY_LOCATION,getX(),getY(),getWidth(),getHeight());

        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        //this.renderListBackground(guiGraphics);

        this.enableScissor(guiGraphics);
        this.renderListItems(guiGraphics, mouseX, mouseY, a);
        guiGraphics.disableScissor();

        this.renderScrollbar(guiGraphics);
    }
    @Override
    public int getRowTop(int i) {
        return this.getY() - (int)this.scrollAmount() + i * this.itemHeight;
    }
    @Override
    protected void renderListItems(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        int rowLeft = this.getRowLeft();
        int rowWidth = this.getRowWidth();
        int itemHeight = this.itemHeight;
        int itemCount = this.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            int rowTop = this.getRowTop(i);
            int rowBottom = this.getRowBottom(i);
            if (rowBottom >= this.getY() && rowTop <= this.getBottom()) {
                this.renderItem(guiGraphics, mouseX, mouseY, a, i, rowLeft, rowTop, rowWidth, itemHeight);
            }
        }
    }
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean bl = this.updateScrolling(d, e, i);
        return super.mouseClicked(d, e, i) || bl;
    }
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.scrolling) {
            if (e < this.getY()) {
                this.setScrollAmount(0.0);
            } else if (e > this.getBottom()) {
                this.setScrollAmount(this.maxScrollAmount());
            } else {
                double h = Math.max(1, this.maxScrollAmount());
                int j = this.scrollerHeight();
                double k = Math.max(1.0, h / (scrollBarHeight() - j));
                this.setScrollAmount(this.scrollAmount() + g * k);
            }

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
