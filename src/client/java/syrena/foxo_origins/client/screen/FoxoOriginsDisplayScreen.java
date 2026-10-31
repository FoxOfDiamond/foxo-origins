package syrena.foxo_origins.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.client.hud.widget.FoxoCenteredLayout;

public abstract class FoxoOriginsDisplayScreen extends Screen {
    public static final ResourceLocation WINDOW_FRAME_LOCATION = FoxoOrigins.id("choose_origin/frame");
    public static final ResourceLocation WINDOW_BACKGROUND_LOCATION = FoxoOrigins.id("choose_origin/background");
    public static final ResourceLocation LIST_ENTRY_LOCATION = FoxoOrigins.id("choose_origin/list/entry/entry");
    public static final ResourceLocation LIST_EXPANDED_ENTRY_LOCATION = FoxoOrigins.id("choose_origin/list/entry/expanded_entry");
    public static final ResourceLocation LIST_SEPARATOR_LOCATION = FoxoOrigins.id("choose_origin/list/separator");
    public static final ResourceLocation SCROLLBAR_SCROLLER_LOCATION = FoxoOrigins.id("choose_origin/list/scrollbar/normal");
    public static final ResourceLocation SCROLLBAR_SCROLLER_PRESSED_LOCATION = FoxoOrigins.id("choose_origin/list/scrollbar/pressed");
    public static final ResourceLocation SCROLLBAR_LOCATION = FoxoOrigins.id("choose_origin/list/scrollbar/slot");
    public static final ResourceLocation IMPACT_PATH = FoxoOrigins.id("choose_origin/list/entry/impact");
    public static final ResourceLocation ENTRY_VIEW_BUTTON_LOCATION = FoxoOrigins.id("choose_origin/list/entry/button/view_button");
    public static final Vector2i WINDOW_SIZE = new Vector2i(308,220);
    public static final Vector2i WINDOW_TITLE_SIZE = new Vector2i(288,24);
    public static final Vector2i WINDOW_TITLE_OFFSET = new Vector2i(10,10);
    public static final Vector2i WINDOW_PAPER_DOLL_SIZE = new Vector2i(96,168);
    public static final Vector2i WINDOW_PAPER_DOLL_OFFSET = new Vector2i(10,42);
    public static final Vector2i LIST_SIZE = new Vector2i(185,168);
    public static final Vector2i WINDOW_LIST_OFFSET = new Vector2i(113,42);
    public static final Vector2i IMPACT_SIZE = new Vector2i(48,8);
    public static final int ENTRY_HEIGHT = 32;
    public static final int LIST_SEPARATOR_HEIGHT = 2;
    public static final Vector2i ENTRY_DESCRIPTION_MARGINS = new Vector2i(10,3);
    public static final int ENTRY_DESCRIPTION_PARAGRAPH_SPACING = 3;
    public static final int ENTRY_DESCRIPTION_LINE_SPACING = 2;
    public static final int ENTRY_DESCRIPTION_LINE_INDENT = 3;
    public static final Vector2i ENTRY_VIEW_BUTTON_OFFSET = new Vector2i(161,8);
    public static final Vector2i ENTRY_VIEW_BUTTON_SIZE = new Vector2i(16,16);
    public static final Vector2i LIST_SCROLLBAR_OFFSET = new Vector2i(210,17);
    public static final Vector2i SCROLLBAR_SCROLLER_SIZE = new Vector2i(8,27);
    public static final Vector2i SCROLLBAR_SIZE = new Vector2i(8,134);
    protected final FoxoCenteredLayout layout = new FoxoCenteredLayout(0,0,WINDOW_SIZE.x,WINDOW_SIZE.y);
    private final Screen lastScreen;
    public FoxoOriginsDisplayScreen(Component component) {
        super(component);
        this.lastScreen = Minecraft.getInstance().screen;
    }
    public FoxoOriginsDisplayScreen(Component component,Screen lastScreen) {
        super(component);
        this.lastScreen = lastScreen;
    }
    @Override
    protected void init(){
        //this.layout.addChild(ImageWidget.sprite(WINDOW_SIZE.x,WINDOW_SIZE.y,WINDOW_FRAME_LOCATION),0,0);
        this.layout.addAnchoredChild(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose()).width(200).build(),WINDOW_SIZE.x/2,WINDOW_SIZE.y - 10,0.5f,0);
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float a){
        super.render(guiGraphics,mouseX,mouseY,a);
    }
    @Override
    protected void repositionElements(){
        layout.setPosition(this.width/2 - layout.getWidth()/2,this.height/2 - layout.getHeight()/2);
        this.layout.arrangeElements();
    }
    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.lastScreen);
    }
    public static ResourceLocation getImpactPath(int i){
        return IMPACT_PATH.withSuffix("/"+i);
    }
}
