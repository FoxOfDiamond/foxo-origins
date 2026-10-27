package syrena.foxo_origins.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.client.hud.widget.FoxoCenteredLayout;
import syrena.foxo_origins.client.hud.widget.FoxoOriginsList;
import syrena.foxo_origins.client.hud.widget.FoxoOriginsListEntry;

public class FoxoOriginsDisplayScreen extends Screen {
    public static final ResourceLocation WINDOW_FRAME_LOCATION = FoxoOrigins.id("choose_origin/frame");
    public static final ResourceLocation WINDOW_BACKGROUND_LOCATION = FoxoOrigins.id("choose_origin/background");
    public static final ResourceLocation LIST_ENTRY_LOCATION = FoxoOrigins.id("choose_origin/list_entry");
    public static final ResourceLocation SCROLLBAR_SCROLLER_LOCATION = FoxoOrigins.id("choose_origin/scrollbar/normal");
    public static final ResourceLocation SCROLLBAR_SCROLLER_PRESSED_LOCATION = FoxoOrigins.id("choose_origin/scrollbar/pressed");
    public static final ResourceLocation SCROLLBAR_LOCATION = FoxoOrigins.id("choose_origin/scrollbar/slot");
    public static final Vector2i WINDOW_SIZE = new Vector2i(308,220);
    public static final Vector2i WINDOW_TITLE_SIZE = new Vector2i(288,24);
    public static final Vector2i WINDOW_TITLE_OFFSET = new Vector2i(10,10);
    public static final Vector2i WINDOW_PAPER_DOLL_SIZE = new Vector2i(96,168);
    public static final Vector2i WINDOW_PAPER_DOLL_OFFSET = new Vector2i(10,42);
    public static final Vector2i LIST_SIZE = new Vector2i(185,168);
    public static final Vector2i WINDOW_LIST_OFFSET = new Vector2i(113,42);
    public static final Vector2i LIST_ENTRY_SIZE = new Vector2i(185,42);
    public static final Vector2i LIST_SCROLLBAR_OFFSET = new Vector2i(170,17);
    public static final Vector2i SCROLLBAR_SCROLLER_SIZE = new Vector2i(8,27);
    public static final Vector2i SCROLLBAR_SIZE = new Vector2i(8,134);
    private final FoxoCenteredLayout layout = new FoxoCenteredLayout(0,0,WINDOW_SIZE.x,WINDOW_SIZE.y);
    public FoxoOriginsDisplayScreen(Component component) {
        super(component);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float a){
        super.render(guiGraphics,mouseX,mouseY,a);
    }
    @Override
    protected void init(){
        FoxoOriginsList list = this.layout.addChild(new FoxoOriginsList(minecraft, LIST_SIZE.x, LIST_SIZE.y,LIST_ENTRY_SIZE.y),WINDOW_LIST_OFFSET.x,WINDOW_LIST_OFFSET.y);
        ImageWidget background = this.layout.addChild(ImageWidget.sprite(WINDOW_SIZE.x,WINDOW_SIZE.y,WINDOW_FRAME_LOCATION),0,0);


        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }
    @Override
    protected void repositionElements(){
        layout.setPosition(this.width/2 - layout.getWidth()/2,this.height/2 - layout.getHeight()/2);
        this.layout.arrangeElements();
    }
}
