package net.shirojr.fallflyingrestrictions.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictionsClient;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

public record ClearZoneCachePacket() implements CustomPayload {
    public static final Id<ClearZoneCachePacket> IDENTIFIER =
            new Id<>(ChannelIdentifiers.CLEAR_ZONE_CACHE_S2C);

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public static final PacketCodec<RegistryByteBuf, ClearZoneCachePacket> CODEC = PacketCodec.unit(new ClearZoneCachePacket());

    public void sendPacket(ServerPlayerEntity targetedPlayer) {
        ServerPlayNetworking.send(targetedPlayer, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        FallFlyingRestrictionsClient.CACHED_ZONES.clear();
    }
}
