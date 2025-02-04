package net.kings_world.fabric_mod.mixins;

import net.kings_world.fabric_mod.Events;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.kings_world.fabric_mod.Main.logger;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"), method = "grantCriterion")
    private void onAdvancementBroadcast(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        AdvancementDisplay display = advancement.getDisplay();
        if (display == null) {
            logger.warn("Advancement {} has no display!", advancement.getId().toString());
            return;
        }
        logger.info("{} unlocked {}", owner.getName().getString(), display.getTitle().getString());
        Events.PLAYER_ADVANCEMENT.invoker().onPlayerAdvancement(owner, display);
    }
}