package net.shirojr.fallflyingrestrictions.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void preventItemUsage(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (allowUsage(stack, user)) return;
        if (ConfigInit.CONFIG.displayWarning.enabledBlockedItemUsage()) {
            user.sendMessage(Text.translatable("notification.fallflyingrestrictions.blocked_item_usage"), true);
        }
        cir.setReturnValue(TypedActionResult.pass(stack));
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void preventItemUsageOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        PlayerEntity user = context.getPlayer();
        if (allowUsage(stack, user) || user == null) return;
        if (ConfigInit.CONFIG.displayWarning.enabledBlockedItemUsage()) {
            user.sendMessage(Text.translatable("notification.fallflyingrestrictions.blocked_item_usage"), true);
        }
        cir.setReturnValue(ActionResult.PASS);
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void preventItemUsageOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (allowUsage(stack, user)) return;
        if (ConfigInit.CONFIG.displayWarning.enabledBlockedItemUsage()) {
            user.sendMessage(Text.translatable("notification.fallflyingrestrictions.blocked_item_usage"), true);
        }
        cir.setReturnValue(ActionResult.PASS);
    }

    @Unique
    private boolean allowUsage(ItemStack stack, LivingEntity entity) {
        if (stack.getItem() instanceof FireworkRocketItem) {
            return !ConfigInit.CONFIG.toggleFeatures.enableRocketUsageBlock();
        } else if (!ConfigInit.CONFIG.toggleFeatures.enabledItemUsageBlock()) return true;
        if (stack.getItem().getUseAction(stack).equals(UseAction.EAT)) return true;
        if (!entity.isFallFlying()) return true;
        return stack.getItem().getUseAction(stack).equals(UseAction.DRINK);
    }
}
