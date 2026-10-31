package syrena.foxo_origins.client.hud.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FoxoCenteredLayout implements Layout {
    private int x;
    private int y;
    private int width;
    private int height;
    private final List<ChildElement<AbstractWidget>> children = new ArrayList<>();
    public FoxoCenteredLayout(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
    public void arrangeElements(){
        children.forEach(child -> {
            child.repositionChild(this.getX(),this.getY());
        });
    }
    public <E extends AbstractWidget> E addChild(E widget, int x, int y){
        children.add(new ChildElement<>(widget,x,y));
        return widget;
    }
    public <E extends AbstractWidget> E addAnchoredChild(E widget, int x, int y, float anchorX, float anchorY){
        children.add(new ChildElement<>(new FoxoAnchorWidget<>(widget,anchorX,anchorY),x,y));
        return widget;
    }
    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        children.forEach(consumer);
    }
    @Override
    public void setX(int i){
        this.x = i;
    }
    @Override
    public void setY(int i){
        this.y = i;
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
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public static class ChildElement <E extends AbstractWidget> implements LayoutElement {
        private final E element;
        private int x;
        private int y;
        public ChildElement(E element, int x, int y){
            this.element = element;
            this.x = x;
            this.y = y;
        }

        @Override
        public void setX(int i) {
            this.x = i;
        }

        @Override
        public void setY(int i) {
            this.y = i;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        public void repositionChild(int xOffset, int yOffset){
            element.setPosition(x+xOffset,y+yOffset);
        }

        @Override
        public int getWidth() {
            return element.getWidth();
        }

        @Override
        public int getHeight() {
            return element.getHeight();
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {
            consumer.accept(element);
        }
    }
}
