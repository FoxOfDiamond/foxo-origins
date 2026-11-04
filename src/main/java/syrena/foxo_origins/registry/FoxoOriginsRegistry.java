package syrena.foxo_origins.registry;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.attachment.entity.FoxoOriginsAttachment;
import syrena.foxo_origins.command.argument.OriginArgument;
import syrena.foxo_origins.command.argument.OriginLayerArgument;
import syrena.foxo_origins.origin.OriginHolder;
import syrena.foxo_origins.origin_layer.OriginLayerHolder;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class FoxoOriginsRegistry {
    public static final ResourceKey<Registry<OriginHolder>> ORIGIN = create("origin");
    public static final ResourceKey<Registry<OriginLayerHolder>> ORIGIN_LAYER = create("origin_layer");
    public static final AttachmentType<Map<OriginLayerHolder,OriginHolder>> ORIGIN_ATTACHMENT = AttachmentRegistry.create(FoxoOrigins.id("origin"), builder -> builder
            .persistent(FoxoOriginsAttachment.CODEC)
            .syncWith(FoxoOriginsAttachment.STREAM_CODEC, AttachmentSyncPredicate.all())
            .copyOnDeath()
    );

    private static <T> ResourceKey<Registry<T>> create(String path) {
        return ResourceKey.createRegistryKey(FoxoOrigins.id(path));
    }

    public static void registerAll(){
        ArgumentTypeRegistry.registerArgumentType(FoxoOrigins.id("origin"), OriginArgument.class, OriginArgument.Info.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(FoxoOrigins.id("origin_layer"), OriginLayerArgument.class, OriginLayerArgument.Info.INSTANCE);
    }
}
