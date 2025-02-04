package net.kings_world.fabric_mod.mixins;

import net.kings_world.fabric_mod.Events;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(at = @At(value = "RETURN"), method = "getDeathMessage")
    private void onGetDeathMessage(CallbackInfoReturnable<Text> cir) {
        if (entity instanceof ServerPlayerEntity player) {
            Events.PLAYER_DEATH.invoker().onPlayerDeath(player, cir.getReturnValue());
        }
    }
}