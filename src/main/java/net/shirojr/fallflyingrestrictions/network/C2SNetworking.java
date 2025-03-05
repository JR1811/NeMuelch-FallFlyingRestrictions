package net.shirojr.fallflyingrestrictions.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.shirojr.fallflyingrestrictions.network.packet.ConfigUpdateRequestPacket;

public class C2SNetworking {

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(ConfigUpdateRequestPacket.IDENTIFIER, ConfigUpdateRequestPacket::handlePacket);
    }

    public static void sendServerConfigUpdateRequest() {
        new ConfigUpdateRequestPacket().sendPacket();
    }
}
