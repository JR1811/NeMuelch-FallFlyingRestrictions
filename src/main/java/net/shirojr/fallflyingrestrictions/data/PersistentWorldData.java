package net.shirojr.fallflyingrestrictions.data;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.data.shape.BoxShape;
import net.shirojr.fallflyingrestrictions.data.shape.SphereShape;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PersistentWorldData extends PersistentState {
    private final List<VolumeData> noFlyingZones = new ArrayList<>();

    public void modifyNoFlyingZones(Consumer<List<VolumeData>> zones, MinecraftServer server) {
        zones.accept(this.noFlyingZones);
        PlayerLookup.all(server).forEach(player -> {
            // zone cache update
            int zoneListSize = this.getNoFlyingZones().size();
            PacketByteBuf zoneBuf = PacketByteBufs.create();
            zoneBuf.writeVarInt(zoneListSize);
            for (VolumeData entry : this.getNoFlyingZones()) {
                entry.toPacketByteBuf(zoneBuf);
            }
            ServerPlayNetworking.send(player, ChannelIdentifiers.UPDATE_ZONE_CACHE_S2C, zoneBuf);
        });
        markDirty();
    }

    public List<VolumeData> getNoFlyingZones() {
        return Collections.unmodifiableList(noFlyingZones);
    }

    public static boolean canStartFlying(LivingEntity entity, List<VolumeData> list) {
        for (VolumeData entry : list) {
            if (!entry.dimension().equals(entity.getWorld().getRegistryKey())) continue;
            if (entry.volume().contains(entity.getBlockPos())) {
                return !entry.volume().preventStartFlying();
            }
        }
        return !ConfigInit.CONFIG.globalZoneRestrictions.preventStartFlying();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean interruptFlying(LivingEntity entity, List<VolumeData> list) {
        for (VolumeData entry : list) {
            if (entry.volume().contains(entity.getBlockPos())) {
                if (!entry.dimension().equals(entity.getWorld().getRegistryKey())) continue;
                return entry.volume().interruptFlying();
            }
        }
        return ConfigInit.CONFIG.globalZoneRestrictions.interruptFlying();
    }

    public static PersistentWorldData fromNbt(NbtCompound nbt) {
        PersistentWorldData persistentData = new PersistentWorldData();

        NbtCompound noFlyingZonesNbt = nbt.getCompound("noFlyingZones");
        for (String key : noFlyingZonesNbt.getKeys()) {
            Identifier identifier = new Identifier(key);
            NbtCompound shapeContent = noFlyingZonesNbt.getCompound(key);
            Identifier dimension = new Identifier(shapeContent.getString("dimension"));

            if (identifier.equals(BoxShape.IDENTIFIER)) {
                persistentData.noFlyingZones.add(new VolumeData(identifier, BoxShape.fromNbt(shapeContent), RegistryKey.of(RegistryKeys.WORLD, dimension)));
            } else if (identifier.equals(SphereShape.IDENTIFIER)) {
                persistentData.noFlyingZones.add(new VolumeData(identifier, SphereShape.fromNbt(shapeContent), RegistryKey.of(RegistryKeys.WORLD, dimension)));
            }
        }
        persistentData.markDirty();
        return persistentData;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound noFlyingZonesNbt = new NbtCompound();
        for (VolumeData entry : this.noFlyingZones) {
            NbtCompound shapeContent = entry.volume().toNbt();
            shapeContent.putString("dimension", entry.dimension().getValue().toString());
            noFlyingZonesNbt.put(entry.identifier().toString(), shapeContent);
        }
        nbt.put("noFlyingZones", noFlyingZonesNbt);
        return nbt;
    }

    public static PersistentWorldData getServerState(MinecraftServer server, RegistryKey<World> worldKey) {
        ServerWorld world = server.getWorld(worldKey);
        if (world == null) {
            throw new RuntimeException("Couldn't load [%s] for persistent state. (%s)".formatted(worldKey.getValue().toString(), FallFlyingRestrictions.MOD_ID));
        }
        PersistentStateManager manager = world.getPersistentStateManager();
        PersistentWorldData data = manager.getOrCreate(PersistentWorldData::fromNbt, PersistentWorldData::new, FallFlyingRestrictions.MOD_ID);
        data.markDirty();
        return data;
    }
}
