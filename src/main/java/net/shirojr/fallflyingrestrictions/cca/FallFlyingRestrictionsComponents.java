package net.shirojr.fallflyingrestrictions.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.shirojr.fallflyingrestrictions.cca.component.ZoneComponent;
import net.shirojr.fallflyingrestrictions.cca.implementation.RestrictedZoneImpl;

public class FallFlyingRestrictionsComponents implements WorldComponentInitializer {
    public static final ComponentKey<ZoneComponent> ZONES = ComponentRegistry.getOrCreate(ZoneComponent.KEY, ZoneComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(ZONES, RestrictedZoneImpl::new);
    }
}
