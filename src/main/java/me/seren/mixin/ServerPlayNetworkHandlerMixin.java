package me.seren.mixin;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.seren.KingsWorld;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected")
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        KingsWorld.LOGGER.info(String.format("%s left the server", this.player.getEntityName()));

        if (KingsWorld.Webhook != null) {
            WebhookMessageBuilder builder = new WebhookMessageBuilder()
                    .setContent(String.format(":arrow_left: %s has left!", this.player.getEntityName()))
                    .setUsername(this.player.getEntityName())
                    .setAvatarUrl(KingsWorld.SERVER_CONFIG.getPlayerAvatar(player));

            KingsWorld.Webhook.send(builder.build());
        }
    }

//    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onChatMessage()V"), method = "onChatMessage")
//    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo info) {
//        System.out.printf("ServerPlayNetworkHandler.onDisconnected(%s)", packet.getChatMessage());
//    }
}
