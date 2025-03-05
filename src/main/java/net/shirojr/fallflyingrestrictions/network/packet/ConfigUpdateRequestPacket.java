package net.shirojr.fallflyingrestrictions.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

public record ConfigUpdateRequestPacket() implements CustomPayload {
    public static final CustomPayload.Id<ConfigUpdateRequestPacket> IDENTIFIER =
            new CustomPayload.Id<>(ChannelIdentifiers.CONFIG_UPDATE_REQUEST_C2S);

    public static final PacketCodec<RegistryByteBuf, ConfigUpdateRequestPacket> CODEC =
            PacketCodec.unit(new ConfigUpdateRequestPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        new ConfigUpdateResponsePacket(
                ConfigInit.CONFIG.displayWarning,
                ConfigInit.CONFIG.toggleFeatures,
                ConfigInit.CONFIG.restrictionValues)
                .sendPacket(context.player());
    }
}
