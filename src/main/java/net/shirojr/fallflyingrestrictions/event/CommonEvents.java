package net.shirojr.fallflyingrestrictions.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.fallflyingrestrictions.command.ZoneRestrictionCommands;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.data.PersistentWorldData;
import net.shirojr.fallflyingrestrictions.network.packet.ClearZoneCachePacket;
import net.shirojr.fallflyingrestrictions.network.packet.ConfigUpdateResponsePacket;
import net.shirojr.fallflyingrestrictions.network.packet.UpdateZoneCachePacket;

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
            new ConfigUpdateResponsePacket(
                    ConfigInit.CONFIG.displayWarning,
                    ConfigInit.CONFIG.toggleFeatures,
                    ConfigInit.CONFIG.restrictionValues)
                    .sendPacket(handler.player);

            PersistentWorldData persistentWorldData = PersistentWorldData.getServerState(server, handler.player.getWorld().getRegistryKey());
            new UpdateZoneCachePacket(persistentWorldData.getNoFlyingZones().size(), persistentWorldData.getNoFlyingZones()).sendPacket(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> new ClearZoneCachePacket().sendPacket(handler.player));
    }

    private static void handleSleepEvent() {
        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return;
            new ConfigUpdateResponsePacket(
                    ConfigInit.CONFIG.displayWarning,
                    ConfigInit.CONFIG.toggleFeatures,
                    ConfigInit.CONFIG.restrictionValues
            ).sendPacket(player);
        });
    }

    public static void initialize() {
        // static initialisation
    }
}
