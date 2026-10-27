package syrena.foxo_origins.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.hud.Sprite;
import io.github.eggohito.neo_apoli.util.ParsedArgument;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

public class FoxoOriginsCodecs {
    public static final Codec<Holder<Origin>> ORIGIN_HOLDER_CODEC = RegistryFixedCodec.create(FoxoOriginsRegistry.ORIGIN);
    public static final Codec<Holder<OriginLayer>> ORIGIN_LAYER_HOLDER_CODEC = RegistryFixedCodec.create(FoxoOriginsRegistry.ORIGIN_LAYER);

    public static final StreamCodec<RegistryFriendlyByteBuf,Holder<Origin>> ORIGIN_HOLDER_STREAM_CODEC = ByteBufCodecs.holder(FoxoOriginsRegistry.ORIGIN,Origin.STREAM_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf,Holder<OriginLayer>> ORIGIN_LAYER_HOLDER_STREAM_CODEC = ByteBufCodecs.holder(FoxoOriginsRegistry.ORIGIN_LAYER,OriginLayer.STREAM_CODEC);

    public static final Codec<ParsedArgument<PowerArgument.Result>> POWER_OR_TAG_ARGUMENT = ParsedArgument.codec(PowerArgument.powerOrTag());

}
