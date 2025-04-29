package net.shirojr.fallflyingrestrictions.data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;
import net.shirojr.fallflyingrestrictions.data.shape.BoxShape;
import net.shirojr.fallflyingrestrictions.data.shape.SphereShape;
import net.shirojr.fallflyingrestrictions.data.shape.Volume;

import java.util.NoSuchElementException;

public record VolumeData(Identifier identifier, Volume volume, RegistryKey<World> dimension) {
    public void toPacketByteBuf(PacketByteBuf buf) {
        buf.writeIdentifier(identifier);
        volume.toPacketByteBuf(buf);
        buf.writeRegistryKey(dimension);
    }

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
}
