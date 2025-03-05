package net.shirojr.fallflyingrestrictions;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.shirojr.fallflyingrestrictions.data.VolumeData;
import net.shirojr.fallflyingrestrictions.network.S2CNetworking;

import java.util.ArrayList;
import java.util.List;

public class FallFlyingRestrictionsClient implements ClientModInitializer {
    public static List<VolumeData> CACHED_ZONES = new ArrayList<>();


    @Override
    public void onInitializeClient() {
        S2CNetworking.initialize();
    }

    public static boolean isClientPlayer(LivingEntity entity) {
        return entity.equals(MinecraftClient.getInstance().player);
    }
}
