package net.shirojr.fallflyingrestrictions.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.GlobalZoneRestrictionData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;

public class S2CNetworking {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C, S2CNetworking::handleConfigUpdateResponse);
    }

    private static void handleConfigUpdateResponse(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        WarningData warningData = WarningData.fromPacketByteBuf(buf);
        FeatureToggleData toggleData = FeatureToggleData.fromPacketByteBuf(buf);
        FlyingBlockHeightData flyingBlockHeightData = FlyingBlockHeightData.fromPacketByteBuf(buf);
        GlobalZoneRestrictionData globalZoneRestrictionData = GlobalZoneRestrictionData.fromPacketByteBuf(buf);

        client.execute(() -> {
            ConfigInit.CONFIG.displayWarning = warningData;
            ConfigInit.CONFIG.toggleFeatures = toggleData;
            ConfigInit.CONFIG.restrictionValues = flyingBlockHeightData;
            ConfigInit.CONFIG.globalZoneRestrictions = globalZoneRestrictionData;
        });
    }
}
