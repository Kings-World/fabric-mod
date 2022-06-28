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
        ExampleMod.LOGGER.info(String.format("%s (%s) joined the server", player.getEntityName(), player.getUuidAsString()));

        if (ExampleMod.Webhook != null) {
            WebhookMessageBuilder builder = new WebhookMessageBuilder()
                    .setContent(String.format(":arrow_right: %s has joined!", player.getEntityName()))
                    .setUsername(player.getEntityName())
                    .setAvatarUrl(ExampleMod.SERVER_CONFIG.getPlayerAvatar(player.getUuid()));

            ExampleMod.Webhook.send(builder.build());
        }
    }
}
