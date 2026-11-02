package syrena.foxo_origins.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.kaya.context.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import syrena.foxo_origins.attachment.entity.FoxoOriginsAttachment;
import syrena.foxo_origins.client.FoxoOriginsClient;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.util.MiscUtil;

import java.util.Map;

public class MiscUtilClient {
    public static boolean checkIfSelectionFinished(Map<OriginLayer, Origin> attachment){
        if (attachment == null) return false;
        for (OriginLayer layer: OriginLayerManager.getInstance().values()){
            if (layerIsPlaceholder(attachment, layer)){
                FoxoOriginsClient.LOGGER.info("Iz placholds");
                return false;
            }
        }
        FoxoOriginsClient.LOGGER.info("Yay!");
        return true;
    }
    public static boolean layerIsPlaceholder(Map<OriginLayer, Origin> attachment, OriginLayer layer){
        return attachment == null ||!attachment.containsKey(layer) || (attachment.get(layer) == layer.placeholderOrigin().orElse(OriginManager.getInstance().get(FoxoOriginsAttachment.DEFAULT_PLACEHOLDER)) && !layer.origins().elements().isEmpty());
    }
    public static Context thisContext(Minecraft minecraft){
        return MiscUtil.thisContext(minecraft.player,minecraft.level);
    }
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
