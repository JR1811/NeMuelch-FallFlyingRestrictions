package net.shirojr.fallflyingrestrictions.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.cca.FallFlyingRestrictionsComponents;
import net.shirojr.fallflyingrestrictions.cca.component.ZoneComponent;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.GlobalZoneRestrictionData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;
import net.shirojr.fallflyingrestrictions.data.VolumeData;
import net.shirojr.fallflyingrestrictions.network.ChannelIdentifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RestrictedZoneImpl implements ZoneComponent, AutoSyncedComponent {
    private final World world;
    private final List<VolumeData> volumeData;

    public RestrictedZoneImpl(World world) {
        this.world = world;
        this.volumeData = new ArrayList<>();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public List<VolumeData> getVolumes() {
        return Collections.unmodifiableList(volumeData);
    }

    @Override
    public void modifyVolumes(Consumer<List<VolumeData>> consumer, boolean shouldSync, boolean shouldSyncConfig) {
        consumer.accept(volumeData);
        if (shouldSync && world instanceof ServerWorld serverWorld) {
            FallFlyingRestrictionsComponents.ZONES.sync(world);
            if (shouldSyncConfig) {
                for (ServerPlayerEntity player : PlayerLookup.all(serverWorld.getServer())) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    WarningData.toPacketByteBuf(buf, ConfigInit.CONFIG.displayWarning);
                    FeatureToggleData.toPacketByteBuf(buf, ConfigInit.CONFIG.toggleFeatures);
                    FlyingBlockHeightData.toPacketByteBuf(buf, ConfigInit.CONFIG.restrictionValues);
                    GlobalZoneRestrictionData.toPacketByteBuf(buf, ConfigInit.CONFIG.globalZoneRestrictions);
                    ServerPlayNetworking.send(player, ChannelIdentifiers.CONFIG_UPDATE_RESPONSE_S2C, buf);
                }
            }
        }
    }

    @Override
    public boolean canStartFlying(LivingEntity entity) {
        BlockPos pos = entity.getBlockPos();
        for (VolumeData entry : this.getVolumes()) {
            if (!entry.dimension().equals(entity.getWorld().getRegistryKey())) continue;
            if (!entry.volume().contains(pos)) continue;
            return !entry.volume().preventStartFlying();
        }
        return !ConfigInit.CONFIG.globalZoneRestrictions.preventStartFlying();
    }

    @Override
    public boolean shouldInterruptFlying(LivingEntity entity) {
        BlockPos pos = entity.getBlockPos();
        for (VolumeData entry : this.getVolumes()) {
            if (!entry.dimension().equals(entity.getWorld().getRegistryKey())) continue;
            if (!entry.volume().contains(pos)) continue;
            return entry.volume().interruptsFlying();
        }
        return ConfigInit.CONFIG.globalZoneRestrictions.interruptFlying();
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        List<VolumeData> data = VolumeData.fromNbt(nbt);
        this.modifyVolumes(existingData -> {
            existingData.clear();
            existingData.addAll(data);
        }, false, false);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        if (VolumeData.fromNbt(nbt).equals(this.volumeData)) return;
        VolumeData.toNbt(nbt, true, this.volumeData);
    }
}
