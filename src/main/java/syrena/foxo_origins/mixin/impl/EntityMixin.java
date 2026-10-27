package syrena.foxo_origins.mixin.impl;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import syrena.foxo_origins.attachment.entity.FoxoOriginsAttachment;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.HashMap;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method="<init>",at = @At("TAIL"))
    void onInit(EntityType entityType, Level level, CallbackInfo ci){
        Entity thisAsEntity = (Entity) (Object) this;
        thisAsEntity.onAttachedSet(FoxoOriginsRegistry.ORIGIN_ATTACHMENT).register((oldAttachment,newAttachment)->{
            FoxoOriginsAttachment.refreshOrigins(thisAsEntity, newAttachment);
        });
    }
}
