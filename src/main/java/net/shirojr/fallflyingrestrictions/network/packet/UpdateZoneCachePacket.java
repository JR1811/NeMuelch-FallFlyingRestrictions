package net.shirojr.fallflyingrestrictions.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictionsClient;
import net.shirojr.fallflyingrestrictions.data.VolumeData;
import net.shirojr.fallflyingrestrictions.data.shape.BoxShape;
import net.shirojr.fallflyingrestrictions.data.shape.SphereShape;
import net.shirojr.fallflyingrestrictions.data.shape.Volume;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

import java.util.ArrayList;
import java.util.List;

public record UpdateZoneCachePacket(int listSize, List<VolumeData> zones) implements CustomPayload {
    public static final Id<UpdateZoneCachePacket> IDENTIFIER =
            new Id<>(ChannelIdentifiers.UPDATE_ZONE_CACHE_S2C);

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public static final PacketCodec<RegistryByteBuf, UpdateZoneCachePacket> CODEC = PacketCodec.of((value, buf) -> {
        buf.writeVarInt(value.listSize());
        for (VolumeData entry : value.zones()) {
            Volume volume = entry.volume();
            buf.writeIdentifier(entry.identifier());
            volume.toPacketByteBuf(buf);
        }
    }, buf -> {
        int size = buf.readVarInt();
        List<VolumeData> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Identifier identifier = buf.readIdentifier();

            Volume volume = null;
            if (identifier.equals(BoxShape.IDENTIFIER)) {
                volume = BoxShape.fromPacketByteBuf(buf);
            } else if (identifier.equals(SphereShape.IDENTIFIER)) {
                volume = SphereShape.fromPacketByteBuf(buf);
            }
            if (volume == null) continue;

            data.add(new VolumeData(identifier, volume));
        }
        return new UpdateZoneCachePacket(size, data);
    });

    public void sendPacket(ServerPlayerEntity targetedPlayer) {
        ServerPlayNetworking.send(targetedPlayer, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        FallFlyingRestrictionsClient.CACHED_ZONES.clear();
        FallFlyingRestrictionsClient.CACHED_ZONES.addAll(zones());
    }
}
