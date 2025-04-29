package net.shirojr.fallflyingrestrictions.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.fallflyingrestrictions.command.ZoneRestrictionCommands;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.GlobalZoneRestrictionData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;
import net.shirojr.fallflyingrestrictions.data.PersistentWorldData;
import net.shirojr.fallflyingrestrictions.data.VolumeData;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

public class CommonEvents {
    static {
        handlePlayerJoinEvent();
        handleSleepEvent();
        handleCommandRegistrationEvents();
    }

    private static void handleCommandRegistrationEvents() {
        CommandRegistrationCallback.EVENT.register(ZoneRestrictionCommands::register);
    }

    private static void handlePlayerJoinEvent() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // config update
            PacketByteBuf configBuf = PacketByteBufs.create();

            WarningData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.displayWarning);
            FeatureToggleData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.toggleFeatures);
            FlyingBlockHeightData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.restrictionValues);
            GlobalZoneRestrictionData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.globalZoneRestrictions);
            ServerPlayNetworking.send(handler.player, ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C, configBuf);

            // zone cache update
            PersistentWorldData persistentWorldData = PersistentWorldData.getServerState(server, handler.player.getWorld().getRegistryKey());
            int zoneListSize = persistentWorldData.getNoFlyingZones().size();
            PacketByteBuf zoneBuf = PacketByteBufs.create();
            zoneBuf.writeVarInt(zoneListSize);
            for (VolumeData entry : persistentWorldData.getNoFlyingZones()) {
                entry.toPacketByteBuf(zoneBuf);
            }
            ServerPlayNetworking.send(handler.player, ChannelIdentifiers.UPDATE_ZONE_CACHE_S2C, zoneBuf);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                ServerPlayNetworking.send(handler.player, ChannelIdentifiers.CLEAR_ZONE_CACHE_S2C, PacketByteBufs.empty()));
    }

    private static void handleSleepEvent() {
        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return;
            // config update
            PacketByteBuf configBuf = PacketByteBufs.create();
            WarningData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.displayWarning);
            FeatureToggleData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.toggleFeatures);
            FlyingBlockHeightData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.restrictionValues);
            GlobalZoneRestrictionData.toPacketByteBuf(configBuf, ConfigInit.CONFIG.globalZoneRestrictions);
            ServerPlayNetworking.send(player, ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C, configBuf);
        });
    }

    public static void initialize() {
        // static initialisation
    }
}
