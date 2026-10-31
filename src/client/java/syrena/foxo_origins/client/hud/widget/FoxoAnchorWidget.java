package syrena.foxo_origins.client.hud.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class FoxoAnchorWidget<E extends AbstractWidget> extends AbstractWidget {
    private final E element;
    private int x;
    private int y;
    private final float xAnchor;
    private final float yAnchor;
    public FoxoAnchorWidget(E element, float xAnchor, float yAnchor){
        super(0,0,0,0, element.getMessage());
        this.element = element;
        this.xAnchor = xAnchor;
        this.yAnchor = yAnchor;
        this.setX(x);
        this.setY(y);
    }

    @Override
    public void setX(int i) {
        this.x = i;
        element.setX(Math.round(this.x - this.xAnchor * getWidth()));
    }

    @Override
    public void setY(int i) {
        this.y = i;
        element.setY(Math.round(this.y - this.yAnchor * getHeight()));
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
    @Override
    public int getWidth() {
        return element.getWidth();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        element.updateNarration(narrationElementOutput);
    }

    @Override
    public int getHeight() {
        return element.getHeight();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        element.render(guiGraphics,mouseX,mouseY,a);
    }
}
