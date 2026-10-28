package syrena.foxo_origins.attachment.entity;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.entity.MutablePowers;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.*;
import java.util.function.Function;

/**
* @param match Match: layer -> origin
*/
public record FoxoOriginsAttachment(ImmutableMap<ResourceLocation, Origin> origins, ImmutableMap<ResourceLocation, OriginLayer> layers, ImmutableMap<ResourceLocation,ResourceLocation> match, Optional<String> decodingErrors) {
    public static final ResourceLocation LOADING_PHASE = FoxoOrigins.id("foxo_origin");
    public static final FoxoOriginsAttachment EMPTY = new FoxoOriginsAttachment();

    public static final Codec<FoxoOriginsAttachment> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<Pair<FoxoOriginsAttachment, T>> decode(DynamicOps<T> ops, T input) {
            return FoxoOriginsAttachment.Entry.LIST_CODEC.parse(ops, input).flatMap(FoxoOriginsAttachment::unpack).map(attachment -> Pair.of(attachment, input));
        }

        @Override
        public <T> DataResult<T> encode(FoxoOriginsAttachment input, DynamicOps<T> ops, T prefix) {
            return input.pack().flatMap(packed -> FoxoOriginsAttachment.Entry.LIST_CODEC.encode(packed, ops, prefix));
        }

    };

    public static final StreamCodec<RegistryFriendlyByteBuf, FoxoOriginsAttachment> STREAM_CODEC = new StreamCodec<>() {

        private static final StreamCodec<RegistryFriendlyByteBuf, Optional<List<FoxoOriginsAttachment.Entry>>> OPTIONAL_ENTRIES_CODEC = ByteBufCodecs.optional(FoxoOriginsAttachment.Entry.LIST_STREAM_CODEC);

        @Override
        public @NotNull FoxoOriginsAttachment decode(RegistryFriendlyByteBuf buf) {
            RegistryOps<Tag> ops = buf.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            return OPTIONAL_ENTRIES_CODEC.decode(buf).flatMap(self -> unpack(self).resultOrPartial()).orElse(EMPTY);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FoxoOriginsAttachment input) {
            RegistryOps<Tag> ops = buf.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            OPTIONAL_ENTRIES_CODEC.encode(buf, input.pack().resultOrPartial());
        }

    };

    public FoxoOriginsAttachment(ImmutableMap<ResourceLocation, Origin> origins, ImmutableMap<ResourceLocation, OriginLayer> layers,ImmutableMap<ResourceLocation, ResourceLocation> match) {
        this(origins, layers, match, Optional.empty());
    }

    public FoxoOriginsAttachment() {
        this(ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), Optional.empty());
    }

    public ResourceLocation getMatchingOriginId(ResourceLocation layerId){
        return this.match.get(layerId);
    }
    public ResourceLocation getLayerId(OriginLayer layer){
        Optional<Map.Entry<ResourceLocation,OriginLayer>> layerEntry = this.layers.entrySet().stream().filter(entry -> entry.getValue().equals(layer)).findFirst();
        return layerEntry.map(Map.Entry::getKey).orElse(null);
    }
    public ResourceLocation getOriginId(Origin origin){
        Optional<Map.Entry<ResourceLocation,Origin>> originEntry = this.origins.entrySet().stream().filter(entry -> entry.getValue().equals(origin)).findFirst();
        return originEntry.map(Map.Entry::getKey).orElse(null);
    }
    public Origin getMatchingOrigin(OriginLayer layer){
        return this.origins.get(this.match.get(getLayerId(layer)));
    }
    public FoxoOriginsAttachment newWithOriginSet(OriginLayer layer, Origin origin){
        List<Entry> entries = new ArrayList<>(List.copyOf(packList()));
        for (int i=0;i<entries.size();i++){
            Entry entry = entries.get(i);
            if (entry.layerId() == getLayerId(layer)){
                Entry packed = new Entry(OriginManager.getInstance().getKey(origin), entry.layerId);
                entries.set(i,packed);
                DataResult<FoxoOriginsAttachment> newAttachment = unpack(entries);
                return newAttachment.getOrThrow();
            }
        }
        entries.add(new Entry(OriginManager.getInstance().getKey(origin), OriginLayerManager.getInstance().getKey(layer)));
        DataResult<FoxoOriginsAttachment> newAttachment = unpack(entries);
        return newAttachment.getOrThrow();
    }
    @Override
    public @NotNull String toString() {
        return "FoxoOriginsAttachment[%s]".formatted(this == EMPTY ? "EMPTY" : "origins=%s, layers=%s, match=%s, decodingErrors=%s".formatted(origins(), layers(),match(), decodingErrors()));
    }


    private Set<Entry> packList(){
        Set<FoxoOriginsAttachment.Entry> entries = new ObjectLinkedOpenHashSet<>();

        this.match.forEach((layerId,originId)->{
            FoxoOriginsAttachment.Entry packed = new FoxoOriginsAttachment.Entry(originId, layerId);
            entries.add(packed);
        });
        return entries;
    }
    private DataResult<List<FoxoOriginsAttachment.Entry>> pack() {
        DataResult<Unit> identity = DataResult.success(Unit.INSTANCE);

        ImmutableList<FoxoOriginsAttachment.Entry> entriesAsList = ImmutableList.copyOf(packList());
        return identity.map(unit -> entriesAsList)
                .setPartial(entriesAsList)
                .map(Function.identity());
    }

    //  TODO:   Pass the identity as the result since decoding/encoding data attachments in FAPI in version
    //          26.1.x now promotes its partial result
    /**
     *  <p>Enforce partial result as success result since FAPI doesn't allow partial results when serializing data
     *  attachments (yet?)</p>
     *
     *  <p>Also cache the errors encountered during decoding to be logged at a later point with the context of the
     *  attachment target (which in this case, is an {@link net.minecraft.world.entity.Entity})</p>
     */
    private static DataResult<FoxoOriginsAttachment> unpack(List<FoxoOriginsAttachment.Entry> entries) {

        Object2ObjectMap<ResourceLocation, Origin> originsMap = new Object2ObjectLinkedOpenHashMap<>();
        Object2ObjectMap<ResourceLocation, OriginLayer> layersMap = new Object2ObjectLinkedOpenHashMap<>();
        Object2ObjectMap<ResourceLocation, ResourceLocation> orderMap = new Object2ObjectLinkedOpenHashMap<>();

        DataResult<Unit> identity = DataResult.success(Unit.INSTANCE);
        FoxoOrigins.LOGGER.info(String.valueOf(entries.size()));
        for (Entry entry : entries) {
            ResourceLocation originId = entry.originId();
            DataResult<Origin> originResult = OriginManager.getInstance().getAsResult(originId);

            identity = identity.apply2stable((unit, origin) -> unit, originResult);

            if (originResult.isError()) {
                continue;
            }

            Origin origin = originResult.getOrThrow();
            originsMap.put(originId, origin);
            //ble
            ResourceLocation layerId = entry.layerId;
            DataResult<OriginLayer> layerResult = OriginLayerManager.getInstance().getAsResult(layerId);

            identity = identity.apply2stable((unit, layer) -> unit, layerResult);

            if (layerResult.isError()) {
                continue;
            }

            OriginLayer layer = layerResult.getOrThrow();
            layersMap.put(layerId, layer);

        }

        FoxoOriginsAttachment result = new FoxoOriginsAttachment(ImmutableMap.copyOf(originsMap), ImmutableMap.copyOf(layersMap), ImmutableMap.copyOf(orderMap), identity.error().map(DataResult.Error::message));
        return DataResult.success(result);
    }

    //  TODO:   Remove this once the codebase has been ported to 26.1.x since decoding/encoding data attachments
    //          in FAPI in that version promotes its partial result
    private static void onLoad(Entity entity, ServerLevel serverLevel) {

        FoxoOriginsAttachment attachment = entity.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT);

        if (attachment != null) {
            attachment.decodingErrors().ifPresent(errors -> FoxoOrigins.logOnce(Level.WARN, "Found error(s) while decoding origins attachment on entity %s: %s".formatted(entity.getName().getString(), errors)));
        }

    }

    record Entry(ResourceLocation originId, ResourceLocation layerId) {

        public static final Codec<FoxoOriginsAttachment.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("origin_id").forGetter(FoxoOriginsAttachment.Entry::originId),
                ResourceLocation.CODEC.fieldOf("layer_id").forGetter(FoxoOriginsAttachment.Entry::layerId)
        ).apply(instance, FoxoOriginsAttachment.Entry::new));

        public static final Codec<List<FoxoOriginsAttachment.Entry>> LIST_CODEC = CODEC.listOf();

        public static final StreamCodec<RegistryFriendlyByteBuf, FoxoOriginsAttachment.Entry> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, FoxoOriginsAttachment.Entry::originId,
                ResourceLocation.STREAM_CODEC, FoxoOriginsAttachment.Entry::layerId,
                FoxoOriginsAttachment.Entry::new
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, List<FoxoOriginsAttachment.Entry>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    }

    static {
        ServerEntityEvents.ENTITY_LOAD.register(LOADING_PHASE, FoxoOriginsAttachment::onLoad);
    }

    public static void refreshOrigins(Entity entity, FoxoOriginsAttachment attachment){
        MutablePowers mutable = MutablePowers.create(entity);
        attachment.match().forEach((layerId,originId)->{
            Origin origin = attachment.origins().get(originId);
            OriginLayer layer = attachment.layers().get(layerId);

            for (PowerHolder<?> power : origin.powers().elements()) {
                OriginLayerManager.getInstance().values().forEach(a->{
                    FoxoOrigins.LOGGER.info(a.toString());
                });
                OriginLayerManager.getInstance().keys().forEach(a->{
                    FoxoOrigins.LOGGER.info(a.toString());
                });
                mutable.grant(power.id(), powerSource(layer));
            }

            mutable.getAllFromSource(powerSource(layer)).forEach(power -> {
                if (!origin.powers().elements().contains(power)){
                    mutable.revoke(power.id(), powerSource(layer));
                }
            });
        });
        mutable.close();
    }
    public static ResourceLocation powerSource(OriginLayer layer){
        return OriginLayerManager.getInstance().getKey(layer);
    }
}
