package net.shirojr.fallflyingrestrictions.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;
import net.shirojr.fallflyingrestrictions.data.shape.BoxShape;
import net.shirojr.fallflyingrestrictions.data.shape.SphereShape;
import net.shirojr.fallflyingrestrictions.data.shape.Volume;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public record VolumeData(Identifier identifier, Volume volume, RegistryKey<World> dimension) {
    public void toPacketByteBuf(PacketByteBuf buf) {
        buf.writeIdentifier(identifier);
        volume.toPacketByteBuf(buf);
        buf.writeRegistryKey(dimension);
    }

    @SuppressWarnings("unused")
    public static VolumeData fromPacketByteBuf(PacketByteBuf buf) {
        Identifier bufIdentifier = buf.readIdentifier();
        Volume bufVolume;
        if (bufIdentifier.equals(BoxShape.IDENTIFIER)) {
            bufVolume = BoxShape.fromPacketByteBuf(buf);
        } else if (bufIdentifier.equals(SphereShape.IDENTIFIER)) {
            bufVolume = SphereShape.fromPacketByteBuf(buf);
        } else {
            NoSuchElementException exception = new NoSuchElementException("No such shape exists: " + bufIdentifier);
            FallFlyingRestrictions.LOGGER.error("Couldn't read network packet", exception);
            throw exception;
        }
        RegistryKey<World> bufDimension = buf.readRegistryKey(RegistryKeys.WORLD);
        return new VolumeData(bufIdentifier, bufVolume, bufDimension);
    }

    public static List<VolumeData> fromNbt(NbtCompound nbt) {
        List<VolumeData> volumeData = new ArrayList<>();
        if (!nbt.contains("volumeData")) return volumeData;

        NbtList volumeDataNbt = nbt.getList("volumeData", NbtElement.COMPOUND_TYPE);
        for (NbtElement elementNbt : volumeDataNbt) {
            NbtCompound entryNbt = (NbtCompound) elementNbt;

            Identifier identifier = Identifier.tryParse(entryNbt.getString("identifier"));
            Identifier dimensionId = Identifier.tryParse(entryNbt.getString("dimension"));
            if (identifier == null || dimensionId == null) continue;

            Volume volume;
            if (identifier.equals(BoxShape.IDENTIFIER)) {
                volume = BoxShape.fromNbt(entryNbt);
            } else if (identifier.equals(SphereShape.IDENTIFIER)) {
                volume = SphereShape.fromNbt(entryNbt);
            } else {
                NoSuchElementException exception = new NoSuchElementException("No such shape exists: " + identifier);
                FallFlyingRestrictions.LOGGER.error("Couldn't read network packet", exception);
                throw exception;
            }
            RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, dimensionId);

            volumeData.add(new VolumeData(identifier, volume, dimension));
        }
        return volumeData;
    }

    public static void toNbt(NbtCompound nbt, boolean clearPrevious, List<VolumeData> volumeData) {
        if (clearPrevious && nbt.contains("volumeData")) {
            nbt.remove("volumeData");
        }
        NbtList volumeDataNbt = nbt.contains("volumeData") ? nbt.getList("volumeData", NbtElement.COMPOUND_TYPE) : new NbtList();
        for (VolumeData entry : volumeData) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString("identifier", entry.identifier.toString());
            entry.volume.toNbt(entryNbt);
            entryNbt.putString("dimension", entry.dimension.getValue().toString());
            volumeDataNbt.add(entryNbt);
        }

        nbt.put("volumeData", volumeDataNbt);
    }
}
