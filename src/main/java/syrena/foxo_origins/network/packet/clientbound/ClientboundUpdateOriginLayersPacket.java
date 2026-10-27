package syrena.foxo_origins.network.packet.clientbound;


import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ClientboundUpdateOriginLayersPacket(Map<ResourceLocation, OriginLayer> originLayers, Map<ResourceLocation, List<OriginLayer>> tags) implements CustomPacketPayload {

    public static final Type<ClientboundUpdateOriginLayersPacket> TYPE = new Type<>(OriginLayerManager.ID.withPath(path -> "clientbound/" + path + "/update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateOriginLayersPacket> CODEC = StreamCodec.ofMember(ClientboundUpdateOriginLayersPacket::send, ClientboundUpdateOriginLayersPacket::receive);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static ClientboundUpdateOriginLayersPacket receive(RegistryFriendlyByteBuf buf) {

        Map<ResourceLocation, OriginLayer> originLayers = new Object2ObjectLinkedOpenHashMap<>();
        int originLayersCount = buf.readInt();

        for (int i = 0; i < originLayersCount; i++) {

            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);

            try {
                originLayers.put(id,OriginLayer.STREAM_CODEC.decode(buf));
            }

            catch (Exception e) {
                FoxoOrigins.LOGGER.error("Couldn't decode origin layer {} during the syncing process", id.toString(), e);
                throw e;
            }

        }

        Map<ResourceLocation, List<OriginLayer>> tags = new Object2ObjectLinkedOpenHashMap<>();
        int tagsCount = buf.readInt();

        for (int i = 0; i < tagsCount; i++) {

            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
            int count = buf.readInt();

            for (int j = 0; j < count; j++) {

                try {

                    ResourceLocation holderId = ResourceLocation.STREAM_CODEC.decode(buf);
                    OriginLayer holder = Objects.requireNonNull(originLayers.get(holderId), "Unknown origin layer" + holderId.toString());

                    tags
                            .computeIfAbsent(id, k -> new ObjectArrayList<>())
                            .add(holder);

                }

                catch (Exception e) {
                    FoxoOrigins.LOGGER.error("Couldn't decode origin layer tag \"{}\" during the syncing process", id, e);
                    throw e;
                }

            }

        }

        return new ClientboundUpdateOriginLayersPacket(originLayers, tags);

    }

    private void send(RegistryFriendlyByteBuf buf) {

        Map<ResourceLocation, OriginLayer> filtered = new Object2ObjectLinkedOpenHashMap<>(originLayers());

        buf.writeInt(filtered.size());

        for (var originLayerEntry : filtered.entrySet()) {

            ResourceLocation id = originLayerEntry.getKey();
            ResourceLocation.STREAM_CODEC.encode(buf, id);

            try {
                OriginLayer.STREAM_CODEC.encode(buf, originLayerEntry.getValue());
            }

            catch (Exception e) {
                FoxoOrigins.LOGGER.error("Couldn't encode origin layer {} during the syncing process", id.toString(), e);
                throw e;
            }

        }

        buf.writeInt(tags().size());

        for (var tagEntry : tags().entrySet()) {

            ResourceLocation id = tagEntry.getKey();
            ResourceLocation.STREAM_CODEC.encode(buf, id);

            List<OriginLayer> holders = tagEntry.getValue();
            buf.writeInt(holders.size());

            for (var holder : holders) {
                ResourceLocation.STREAM_CODEC.encode(buf, id);
            }

        }

    }

}
