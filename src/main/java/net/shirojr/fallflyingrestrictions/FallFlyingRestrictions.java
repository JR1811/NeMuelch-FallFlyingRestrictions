package net.shirojr.fallflyingrestrictions;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.event.CommonEvents;
import net.shirojr.fallflyingrestrictions.network.C2SNetworking;
import net.shirojr.fallflyingrestrictions.network.NetworkPayloads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FallFlyingRestrictions implements ModInitializer {
    public static final String MOD_ID = "fallflyingrestrictions";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        NetworkPayloads.initialize();
        ConfigInit.initialize();
        C2SNetworking.initialize();
        CommonEvents.initialize();

        LOGGER.info("Mayday, we are going down!");
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}