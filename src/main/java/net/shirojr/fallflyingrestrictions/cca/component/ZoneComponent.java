package net.shirojr.fallflyingrestrictions.cca.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;
import net.shirojr.fallflyingrestrictions.cca.FallFlyingRestrictionsComponents;
import net.shirojr.fallflyingrestrictions.data.VolumeData;

import java.util.List;
import java.util.function.Consumer;

public interface ZoneComponent extends Component {
    Identifier KEY = FallFlyingRestrictions.getId("zones");

    static ZoneComponent fromWorld(World world) {
        return FallFlyingRestrictionsComponents.ZONES.get(world);
    }

    @SuppressWarnings("unused")
    World getWorld();

    List<VolumeData> getVolumes();

    void modifyVolumes(Consumer<List<VolumeData>> consumer, boolean shouldSync, boolean shouldSyncConfig);

    boolean canStartFlying(LivingEntity entity);

    boolean shouldInterruptFlying(LivingEntity entity);
}
