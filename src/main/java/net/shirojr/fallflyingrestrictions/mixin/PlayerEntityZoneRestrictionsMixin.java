package net.shirojr.fallflyingrestrictions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
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
        BlockPos pos = player.getBlockPos();
        if (player.getWorld().isClient()) {
            boolean canStart = PersistentWorldData.canStartFlying(pos, FallFlyingRestrictionsClient.CACHED_ZONES);
            if (!canStart && ConfigInit.CONFIG.displayWarning.enabledZoneTakeOffWarning()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.prevent_start_flying"), true);
            }
            return canStart;
        } else {
            MinecraftServer server = player.getServer();
            if (server == null) return original;
            List<VolumeData> zones = PersistentWorldData.getServerState(server, player.getWorld().getRegistryKey()).getNoFlyingZones();
            boolean canStart = PersistentWorldData.canStartFlying(pos, zones);
            if (!canStart && ConfigInit.CONFIG.displayWarning.enabledZoneTakeOffWarning()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.prevent_start_flying"), true);
            }
            return canStart;
        }
    }
}
