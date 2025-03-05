package net.shirojr.fallflyingrestrictions.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.fallflyingrestrictions.network.packet.ClearZoneCachePacket;
import net.shirojr.fallflyingrestrictions.network.packet.ConfigUpdateRequestPacket;
import net.shirojr.fallflyingrestrictions.network.packet.ConfigUpdateResponsePacket;
import net.shirojr.fallflyingrestrictions.network.packet.UpdateZoneCachePacket;
import net.shirojr.fallflyingrestrictions.util.LoggerUtil;

public class NetworkPayloads {
    static {
        registerS2C(ConfigUpdateResponsePacket.IDENTIFIER, ConfigUpdateResponsePacket.CODEC);
        registerS2C(UpdateZoneCachePacket.IDENTIFIER, UpdateZoneCachePacket.CODEC);
        registerS2C(ClearZoneCachePacket.IDENTIFIER, ClearZoneCachePacket.CODEC);

        registerC2S(ConfigUpdateRequestPacket.IDENTIFIER, ConfigUpdateRequestPacket.CODEC);
    }

    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(packetIdentifier, codec);
    }

    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(packetIdentifier, codec);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized payload registering for custom networking");
    }
}
