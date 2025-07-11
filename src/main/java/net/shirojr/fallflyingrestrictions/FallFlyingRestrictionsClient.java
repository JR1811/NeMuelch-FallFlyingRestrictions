package net.shirojr.fallflyingrestrictions;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.fallflyingrestrictions.network.S2CNetworking;

public class FallFlyingRestrictionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        S2CNetworking.initialize();
    }
}
