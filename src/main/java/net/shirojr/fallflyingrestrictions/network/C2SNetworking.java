package net.shirojr.fallflyingrestrictions.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.GlobalZoneRestrictionData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;

public class C2SNetworking {

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(ChannelIdentifiers.CONFIG_UPDATE_REQUEST_C2S, C2SNetworking::handleConfigUpdateRequest);
    }

    private static void handleConfigUpdateRequest(MinecraftServer server, ServerPlayerEntity serverPlayer, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        server.execute(() -> {
            // config update
            PacketByteBuf configBuf = PacketByteBufs.create();
            WarningData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.displayWarning);
            FeatureToggleData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.toggleFeatures);
            FlyingBlockHeightData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.restrictionValues);
            GlobalZoneRestrictionData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.globalZoneRestrictions);
            ServerPlayNetworking.send(serverPlayer, ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C, configBuf);
        });
    }
}
