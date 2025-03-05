package net.shirojr.fallflyingrestrictions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.fallflyingrestrictions.config.ConfigInit;
import net.shirojr.fallflyingrestrictions.util.LoggerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(method = "travel",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    )
    private void badweatherflight$flyingAdjustments(LivingEntity instance, Vec3d vec3d, Operation<Void> original) {
        if (shouldApplyDefaultValue(instance) || (isOptimalFlyingCondition(instance) && !isFlyingTooHigh(instance))) {
            LoggerUtil.devLogger("applying normal flight | " + instance.getWorld());
            original.call(instance, vec3d);
            return;
        }

        Vec3d heightDownForce = Vec3d.ZERO;
        Vec3d badWeatherDownForce = Vec3d.ZERO;

        if (isFlyingTooHigh(instance)) {
            if (ConfigInit.CONFIG.displayWarning.enabledFlyingTooHighWarning() && instance instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.flying_too_high"), true);
            }
            heightDownForce = new Vec3d(0.0, -(ConfigInit.CONFIG.restrictionValues.getFlyingTooHighDownForce()), 0.0);
        }
        if (!isOptimalFlyingCondition(instance)) {
            if (ConfigInit.CONFIG.displayWarning.badWeatherConditionWarning() && instance instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.translatable("notification.fallflyingrestrictions.bad_flying_condition"), true);
            }
            badWeatherDownForce = new Vec3d(0.0, -(ConfigInit.CONFIG.restrictionValues.getBadWeatherDownForce()), 0.0);
        }

        instance.setVelocity(vec3d.add(heightDownForce).add(badWeatherDownForce));
    }

    /**
     * Utility method which considers config entries, type of entity and game mode.
     *
     * @return true, if <b><i>no</i></b> changes to the fall-flying movement should be done.
     */
    @Unique
    private static boolean shouldApplyDefaultValue(LivingEntity instance) {
        if (!ConfigInit.CONFIG.toggleFeatures.enabledMovementChanges()) return true;
        if (!(instance instanceof PlayerEntity player)) return true;
        return player.getAbilities().creativeMode || player.isSpectator();
    }

    /**
     * Utility method which considers weather, safe flight height and a clear view into the sky.
     *
     * @return true, if flying condition are optimal.
     */
    @Unique
    private static boolean isOptimalFlyingCondition(LivingEntity livingEntity) {
        World world = livingEntity.getWorld();
        BlockPos livingEntityPos = livingEntity.getBlockPos();

        boolean doRoofSafetyCheck = ConfigInit.CONFIG.toggleFeatures.enabledRoofAboveHeadSafety();
        boolean doHeightSafetyCheck = ConfigInit.CONFIG.toggleFeatures.enabledSafeFlightHeight();
        int safeBlockHeight = Math.max(ConfigInit.CONFIG.restrictionValues.getSafeAboveGroundHeight(), 1);

        if (!world.isThundering() && !world.isRaining()) return true;
        if (!world.isSkyVisible(livingEntityPos) && doRoofSafetyCheck) return true;
        if (!doHeightSafetyCheck) return false;

        boolean isSafeHeight = false;
        for (int i = 0; i < safeBlockHeight; i++) {
            if (world.getBlockState(livingEntityPos).getBlock() != Blocks.AIR) {
                isSafeHeight = true;
                break;
            }
            livingEntityPos = livingEntityPos.down();
        }
        return isSafeHeight;
    }

    @Unique
    private static boolean isFlyingTooHigh(LivingEntity livingEntity) {
        if (!ConfigInit.CONFIG.toggleFeatures.enabledFlyingTooHigh()) return false;
        return livingEntity.getBlockPos().getY() > ConfigInit.CONFIG.restrictionValues.getFlyingHeightLimit();
    }
}