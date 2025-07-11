package net.shirojr.fallflyingrestrictions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.fallflyingrestrictions.cca.component.ZoneComponent;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityZoneRestrictionsMixin {
    @ModifyExpressionValue(method = "tickFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ElytraItem;isUsable(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean isInNoFlyingZone(boolean original) {
        if (!original) return false;
        LivingEntity entity = (LivingEntity) (Object) this;
        boolean interrupt = ZoneComponent.fromWorld(entity.getWorld()).shouldInterruptFlying(entity);
        if (interrupt && entity instanceof PlayerEntity player && ConfigInit.CONFIG.displayWarning.enabledZoneFlying()) {
            player.sendMessage(Text.translatable("notification.fallflyingrestrictions.restricted_zone.interrupt_flying"), true);
        }
        return !interrupt;
    }
}
