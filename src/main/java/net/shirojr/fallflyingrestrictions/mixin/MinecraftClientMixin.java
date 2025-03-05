package net.shirojr.fallflyingrestrictions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyExpressionValue(
            method = "handleInputEvents",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;inventoryKey:Lnet/minecraft/client/option/KeyBinding;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 0)
    )
    private boolean badweatherflight$blockInventoryScreen(boolean original) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        PlayerEntity clientPlayer = client.player;
        if (clientPlayer == null) return original;

        boolean isSurvival = !clientPlayer.isCreative() && !clientPlayer.isSpectator();

        if (clientPlayer.isFallFlying() && isSurvival && ConfigInit.CONFIG.toggleFeatures.enabledInventoryBlock()) {
            if (client.options.inventoryKey.isPressed() || client.options.inventoryKey.wasPressed()) {
                if (ConfigInit.CONFIG.displayWarning.enabledBlockedInventoryWarning()) {
                    clientPlayer.sendMessage(Text.translatable("notification.fallflyingrestrictions.inventory_block"), true);
                }
                return false;
            }
        }
        return original;
    }
}
