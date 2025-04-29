package net.shirojr.fallflyingrestrictions.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictionsClient;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.GlobalZoneRestrictionData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;
import net.shirojr.fallflyingrestrictions.data.VolumeData;

import java.util.ArrayList;
import java.util.List;

public class S2CNetworking {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C, S2CNetworking::handleConfigUpdateResponse);
        ClientPlayNetworking.registerGlobalReceiver(ChannelIdentifiers.UPDATE_ZONE_CACHE_S2C, S2CNetworking::handleZoneCacheUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ChannelIdentifiers.CLEAR_ZONE_CACHE_S2C, S2CNetworking::handleClearZoneCache);
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

    private static void handleZoneCacheUpdate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int listSize = buf.readVarInt();
        List<VolumeData> list = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            list.add(VolumeData.fromPacketByteBuf(buf));
        }

        client.execute(() -> {
            FallFlyingRestrictionsClient.CACHED_ZONES.clear();
            FallFlyingRestrictionsClient.CACHED_ZONES.addAll(list);
        });
    }

    private static void handleClearZoneCache(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        client.execute(() -> FallFlyingRestrictionsClient.CACHED_ZONES.clear());
    }
}
