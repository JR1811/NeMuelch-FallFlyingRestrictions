package net.shirojr.fallflyingrestrictions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

@Mixin(PlayerEntity.class)
public class PlayerEntityZoneRestrictionsMixin {
    @ModifyExpressionValue(method = "checkFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ElytraItem;isUsable(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean checkNoFlyingZones(boolean original) {
        if (!original) return false;
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient()) {
            boolean canStart = PersistentWorldData.canStartFlying(player, FallFlyingRestrictionsClient.CACHED_ZONES);
            if (!canStart && ConfigInit.CONFIG.displayWarning.enabledZoneTakeOffWarning()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.prevent_start_flying"), true);
            }
            return canStart;
        } else {
            MinecraftServer server = player.getServer();
            if (server == null) return original;
            List<VolumeData> zones = PersistentWorldData.getServerState(server, player.getWorld().getRegistryKey()).getNoFlyingZones();
            boolean canStart = PersistentWorldData.canStartFlying(player, zones);
            if (!canStart && ConfigInit.CONFIG.displayWarning.enabledZoneTakeOffWarning()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.prevent_start_flying"), true);
            }
            return canStart;
        }
    }

    @WrapOperation(method = "startFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setFlag(IZ)V"))
    private void allowStartFlying(PlayerEntity player, int index, boolean startFlying, Operation<Void> original) {
        boolean canStart = true;
        if (player.getWorld().isClient()) {
            canStart = PersistentWorldData.canStartFlying(player, FallFlyingRestrictionsClient.CACHED_ZONES);
            if (!canStart && ConfigInit.CONFIG.displayWarning.enabledZoneTakeOffWarning()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.prevent_start_flying"), true);
            }
        } else {
            MinecraftServer server = player.getServer();
            if (server != null) {
                List<VolumeData> zones = PersistentWorldData.getServerState(server, player.getWorld().getRegistryKey()).getNoFlyingZones();
                canStart = PersistentWorldData.canStartFlying(player, zones);
                if (!canStart && ConfigInit.CONFIG.displayWarning.enabledZoneTakeOffWarning()) {
                    player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.prevent_start_flying"), true);
                }
            }
        }
        if (canStart) {
            original.call(player, index, startFlying);
        }
    }
}
