package syrena.foxo_origins.registry;

import io.github.eggohito.kaya.Kaya;
import io.github.eggohito.kaya.command.argument.PowerArgument;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.attachment.entity.FoxoOriginsAttachment;
import syrena.foxo_origins.command.argument.OriginArgument;
import syrena.foxo_origins.command.argument.OriginLayerArgument;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin_layer.OriginLayer;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class FoxoOriginsRegistry {
    public static final ResourceKey<Registry<Origin>> ORIGIN = create("origin");
    public static final ResourceKey<Registry<OriginLayer>> ORIGIN_LAYER = create("origin_layer");
    public static final AttachmentType<Map<OriginLayer,Origin>> ORIGIN_ATTACHMENT = AttachmentRegistry.create(FoxoOrigins.id("origin"), builder -> builder
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
