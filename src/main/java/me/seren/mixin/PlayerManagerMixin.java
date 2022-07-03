package me.seren.mixin;

import me.seren.Events;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
  @Inject(at = @At("TAIL"), method = "onPlayerConnect")
  private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
    Events.playerJoin(player);
  }
}
