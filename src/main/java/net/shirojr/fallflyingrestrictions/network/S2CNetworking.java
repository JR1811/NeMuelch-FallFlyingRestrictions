package net.shirojr.fallflyingrestrictions.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.shirojr.fallflyingrestrictions.network.packet.ClearZoneCachePacket;
import net.shirojr.fallflyingrestrictions.network.packet.ConfigUpdateResponsePacket;
import net.shirojr.fallflyingrestrictions.network.packet.UpdateZoneCachePacket;

public class S2CNetworking {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigUpdateResponsePacket.IDENTIFIER, ConfigUpdateResponsePacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(UpdateZoneCachePacket.IDENTIFIER, UpdateZoneCachePacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ClearZoneCachePacket.IDENTIFIER, ClearZoneCachePacket::handlePacket);
    }
}
