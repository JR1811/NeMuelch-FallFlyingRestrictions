package net.shirojr.fallflyingrestrictions.data;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.data.shape.Volume;

public record VolumeData(Identifier identifier, Volume volume, RegistryKey<World> dimension) {
}
