package net.shirojr.fallflyingrestrictions.data;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;
import net.shirojr.fallflyingrestrictions.data.shape.BoxShape;
import net.shirojr.fallflyingrestrictions.data.shape.SphereShape;
import net.shirojr.fallflyingrestrictions.network.packet.UpdateZoneCachePacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PersistentWorldData extends PersistentState {
    private final List<VolumeData> noFlyingZones = new ArrayList<>();

    private static final Type<PersistentWorldData> type = new Type<>(
            PersistentWorldData::new,
            (nbt, registryLookup) -> PersistentWorldData.fromNbt(nbt),
            null
    );

    public void modifyNoFlyingZones(Consumer<List<VolumeData>> zones, MinecraftServer server) {
        zones.accept(this.noFlyingZones);
        PlayerLookup.all(server).forEach(player -> new UpdateZoneCachePacket(this.getNoFlyingZones().size(), this.getNoFlyingZones()));
    }

    public List<VolumeData> getNoFlyingZones() {
        return Collections.unmodifiableList(noFlyingZones);
    }

    public static boolean canStartFlying(BlockPos pos, List<VolumeData> list) {
        for (VolumeData data : list) {
            if (data.volume().contains(pos) && data.volume().preventStartFlying()) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean interruptFlying(BlockPos pos, List<VolumeData> list) {
        for (VolumeData entry : list) {
            if (entry.volume().contains(pos) && entry.volume().interruptFlying()) {
                return true;
            }
        }
        return false;
    }

    public static PersistentWorldData fromNbt(NbtCompound nbt) {
        PersistentWorldData persistentData = new PersistentWorldData();

        NbtCompound noFlyingZonesNbt = nbt.getCompound("noFlyingZones");
        for (String key : noFlyingZonesNbt.getKeys()) {
            Identifier identifier = Identifier.of(key);
            NbtCompound shapeContent = noFlyingZonesNbt.getCompound(key);

            if (identifier.equals(BoxShape.IDENTIFIER)) {
                persistentData.noFlyingZones.add(new VolumeData(identifier, BoxShape.fromNbt(shapeContent)));
            } else if (identifier.equals(SphereShape.IDENTIFIER)) {
                persistentData.noFlyingZones.add(new VolumeData(identifier, SphereShape.fromNbt(shapeContent)));
            }
        }
        persistentData.markDirty();
        return persistentData;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound noFlyingZonesNbt = new NbtCompound();
        for (var entry : this.noFlyingZones) {
            noFlyingZonesNbt.put(entry.identifier().toString(), entry.volume().toNbt());
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
        PersistentWorldData data = manager.getOrCreate(type, FallFlyingRestrictions.MOD_ID);
        data.markDirty();
        return data;
    }
}
