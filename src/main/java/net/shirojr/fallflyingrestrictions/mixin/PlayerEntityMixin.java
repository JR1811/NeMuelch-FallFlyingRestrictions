package net.shirojr.fallflyingrestrictions.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void blockItemEating(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!ConfigInit.CONFIG.toggleFeatures.enabledEatingWhileFlyingBlock()) return;
        if (player.isFallFlying() && player.getHungerManager().isNotFull()) {
            if (ConfigInit.CONFIG.displayWarning.enabledEatingWhileFlyingWarning()) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.eating_block"), true);
            }
            cir.setReturnValue(false);
        }
    }
}
