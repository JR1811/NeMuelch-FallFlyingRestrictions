package net.shirojr.fallflyingrestrictions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictionsClient;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.data.PersistentWorldData;
import net.shirojr.fallflyingrestrictions.data.VolumeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityZoneRestrictionsMixin {
    @ModifyExpressionValue(method = "tickFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ElytraItem;isUsable(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean isInNoFlyingZone(boolean original) {
        if (!original) return false;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld().isClient()) {
            if (!FallFlyingRestrictionsClient.isClientPlayer(entity)) return original;
            boolean interrupt = PersistentWorldData.interruptFlying(entity, FallFlyingRestrictionsClient.CACHED_ZONES);
            if (interrupt && entity instanceof PlayerEntity player && ConfigInit.CONFIG.displayWarning.enabledZoneFlying()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.interrupt_flying"), true);
            }
            return !interrupt;
        } else {
            MinecraftServer server = entity.getServer();
            if (server == null) return original;
            List<VolumeData> zones = PersistentWorldData.getServerState(server, entity.getWorld().getRegistryKey()).getNoFlyingZones();
            boolean interrupt = PersistentWorldData.interruptFlying(entity, zones);
            if (interrupt && entity instanceof PlayerEntity player && ConfigInit.CONFIG.displayWarning.enabledZoneFlying()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.interrupt_flying"), true);
            }
            return !interrupt;
        }
    }
}
