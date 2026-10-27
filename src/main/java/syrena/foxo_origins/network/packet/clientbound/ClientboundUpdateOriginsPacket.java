package syrena.foxo_origins.network.packet.clientbound;


import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.codec.FoxoOriginsCodecs;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ClientboundUpdateOriginsPacket(Map<ResourceLocation, Origin> origins, Map<ResourceLocation, List<Origin>> tags) implements CustomPacketPayload {

    public static final Type<ClientboundUpdateOriginsPacket> TYPE = new Type<>(OriginManager.ID.withPath(path -> "clientbound/" + path + "/update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateOriginsPacket> CODEC = StreamCodec.ofMember(ClientboundUpdateOriginsPacket::send, ClientboundUpdateOriginsPacket::receive);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static ClientboundUpdateOriginsPacket receive(RegistryFriendlyByteBuf buf) {

        Map<ResourceLocation, Origin> origins = new Object2ObjectLinkedOpenHashMap<>();
        int originsCount = buf.readInt();

        for (int i = 0; i < originsCount; i++) {

            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);

            try {
                origins.put(id,Origin.STREAM_CODEC.decode(buf));
            }

            catch (Exception e) {
                FoxoOrigins.LOGGER.error("Couldn't decode origin {} during the syncing process", id.toString(), e);
                throw e;
            }

        }

        Map<ResourceLocation, List<Origin>> tags = new Object2ObjectLinkedOpenHashMap<>();
        int tagsCount = buf.readInt();

        for (int i = 0; i < tagsCount; i++) {

            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
            int count = buf.readInt();

            for (int j = 0; j < count; j++) {

                try {

                    ResourceLocation holderId = ResourceLocation.STREAM_CODEC.decode(buf);
                    Origin holder = Objects.requireNonNull(origins.get(holderId), "Unknown origin" + holderId.toString());

                    tags
                            .computeIfAbsent(id, k -> new ObjectArrayList<>())
                            .add(holder);

                }

                catch (Exception e) {
                    FoxoOrigins.LOGGER.error("Couldn't decode origin tag \"{}\" during the syncing process", id, e);
                    throw e;
                }

            }

        }

        return new ClientboundUpdateOriginsPacket(origins, tags);

    }

    private void send(RegistryFriendlyByteBuf buf) {

        Map<ResourceLocation, Origin> filtered = new Object2ObjectLinkedOpenHashMap<>(origins());

        buf.writeInt(filtered.size());

        for (var originEntry : filtered.entrySet()) {

            ResourceLocation id = originEntry.getKey();
            ResourceLocation.STREAM_CODEC.encode(buf, id);

            try {
                Origin.STREAM_CODEC.encode(buf, originEntry.getValue());
            }

            catch (Exception e) {
                FoxoOrigins.LOGGER.error("Couldn't encode origin {} during the syncing process", id.toString(), e);
                throw e;
            }

        }

        buf.writeInt(tags().size());

        for (var tagEntry : tags().entrySet()) {

            ResourceLocation id = tagEntry.getKey();
            ResourceLocation.STREAM_CODEC.encode(buf, id);

            List<Origin> holders = tagEntry.getValue();
            buf.writeInt(holders.size());

            for (var holder : holders) {
                ResourceLocation.STREAM_CODEC.encode(buf, id);
            }

        }

    }

}
