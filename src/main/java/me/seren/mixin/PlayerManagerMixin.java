package me.seren.mixin;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.seren.ExampleMod;
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
        ExampleMod.LOGGER.info("A user joined the server for the first time");
        ExampleMod.LOGGER.info(player.getEntityName());

        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setContent(String.format(":arrow_right: %s has joined!", player.getEntityName()))
                .setUsername(player.getEntityName())
                .setAvatarUrl(String.format("https://crafthead.net/helm/%s", player.getUuidAsString()));

        ExampleMod.Webhook.send(builder.build());
    }
}
