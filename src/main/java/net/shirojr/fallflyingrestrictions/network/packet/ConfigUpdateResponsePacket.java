package net.shirojr.fallflyingrestrictions.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

public record ConfigUpdateResponsePacket(WarningData warningData, FeatureToggleData toggleData,
                                         FlyingBlockHeightData flyingBlockHeightData) implements CustomPayload {

    public static final Id<ConfigUpdateResponsePacket> IDENTIFIER =
            new Id<>(ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C);

    public static final PacketCodec<RegistryByteBuf, ConfigUpdateResponsePacket> CODEC =
            PacketCodec.of((value, buf) -> {
                WarningData.toPacketByteBuf(buf, value.warningData());
                FeatureToggleData.toPacketByteBuf(buf, value.toggleData());
                FlyingBlockHeightData.toPacketByteBuf(buf, value.flyingBlockHeightData());
            }, buf -> new ConfigUpdateResponsePacket(WarningData.fromPacketByteBuf(buf), FeatureToggleData.fromPacketByteBuf(buf), FlyingBlockHeightData.fromPacketByteBuf(buf)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket(ServerPlayerEntity targetedPlayer) {
        ServerPlayNetworking.send(targetedPlayer, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ConfigInit.CONFIG.displayWarning = this.warningData;
        ConfigInit.CONFIG.toggleFeatures = this.toggleData;
        ConfigInit.CONFIG.restrictionValues = this.flyingBlockHeightData;
    }
}
