package syrena.foxo_origins.client.mixin.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import syrena.foxo_origins.client.FoxoOriginsClient;
import syrena.foxo_origins.client.screen.OriginLayersSelectScreen;
import syrena.foxo_origins.client.util.MiscUtilClient;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method="<init>",at = @At("TAIL"))
    void onInit(EntityType entityType, Level level, CallbackInfo ci){
        Entity thisAsEntity = (Entity) (Object) this;
        thisAsEntity.onAttachedSet(FoxoOriginsRegistry.ORIGIN_ATTACHMENT).register((oldAttachment, newAttachment)->{
            FoxoOriginsClient.LOGGER.info("Something changed");
            Minecraft.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (!MiscUtilClient.checkIfSelectionFinished(newAttachment)||Minecraft.getInstance().screen instanceof OriginLayersSelectScreen){
                        Minecraft.getInstance().setScreen(new OriginLayersSelectScreen(Component.translatable("gui.foxo-origins.view_layer.title"), new ObjectArrayList<>(OriginLayerManager.getInstance().values().iterator())));
                    }
                }
            });
        });
    }
}
