package syrena.foxo_origins.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class Misc {
    public static void renderScaledFakeItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, int size){
        float scale = size / 16f;
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(scale,scale,scale);
        pose.translate(-5.5 + 8 * (1/scale) + x / scale, -5.5 + 8 * (1/scale) + y / scale, (float) (-150) * (1-(1/scale)));
        guiGraphics.renderFakeItem(itemStack, 0, 0, 1);
        pose.popPose();
    }
}
