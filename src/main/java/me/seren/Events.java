package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.seren.discord.Client;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;

import javax.security.auth.login.LoginException;

import static me.seren.KingsWorld.*;

public final class Events {
    public static void serverStarting(MinecraftServer server) {
        try {
            WEBHOOK = WebhookClient.withUrl(CONFIG.getWebhookUrl());
            CLIENT = new Client(server);
        } catch (LoginException | InterruptedException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void serverStarted(MinecraftServer server) {
        CLIENT.sendMessage(":white_check_mark: The server has started!");
    }

    public static void serverStopped(MinecraftServer server) {
        CLIENT.sendMessage(":octagonal_sign: The server has stopped!");
    }

    public static void chatMessage(FilteredMessage<SignedMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey) {
        playerMessage(sender, message.raw().getContent().getString());
    }

    public static void playerJoin(ServerPlayerEntity player) {
        sendPlayerWebhook(player, ":arrow_right: $name has joined!");
    }

    public static void playerLeave(ServerPlayerEntity player) {
        sendPlayerWebhook(player, ":arrow_left: $name has left!");
    }

    public static void playerKilled(DamageSource source) {

    }

    public static void playerDeath(DamageSource source) {
        System.out.println(source);
    }

    public static void playerMessage(ServerPlayerEntity player, String message) {
        sendPlayerWebhook(player, "$name: " + message);
    }

    public static void sendPlayerWebhook(ServerPlayerEntity player, String content) {
        content = content
                .replaceAll("\\$name", player.getEntityName());

        if (WEBHOOK == null) return;
        WEBHOOK.send(new WebhookMessageBuilder()
                .setContent(content)
                .setUsername(player.getEntityName())
                .setAvatarUrl(CONFIG.getPlayerAvatar(player))
                .build());
    }
}
