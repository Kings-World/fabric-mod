package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

public final class Events {
    private static Events instance;
    public static final WebhookClient webhook = KingsWorld.Webhook;

    public void startServer() {
        KingsWorld.DiscordClient.sendMessage(":white_check_mark: The server has started!");
//        send(":white_check_mark: The server has started!");
    }

    public void stopServer() {
        KingsWorld.DiscordClient.sendMessage(":octagonal_sign: The server has stopped!");
//        send(":octagonal_sign: The server has stopped!");
    }

    public void playerJoin(ServerPlayerEntity player) {
        send(playerMsg(player, ":arrow_right: $name has joined!"));
    }

    public void playerLeave(ServerPlayerEntity player) {
        send(playerMsg(player, ":arrow_left: $name has left!"));
    }

    public void playerKilled(DamageSource source) {

    }

    public void playerDeath(DamageSource source) {
        System.out.println(source);
    }

    public void playerMessage(ServerPlayerEntity player, String message) {
        send(playerMsg(player, "$name: " + message));
    }

    public static Events getInstance() {
        if (instance == null) instance = new Events();
        return instance;
    }

    public static WebhookMessage playerMsg(ServerPlayerEntity player, String content) {
        content = content
                .replaceAll("\\$name", player.getEntityName());

        return new WebhookMessageBuilder()
                .setContent(content)
                .setUsername(player.getEntityName())
                .setAvatarUrl(KingsWorld.SERVER_CONFIG.getPlayerAvatar(player))
                .build();
    }

    public void send(WebhookMessage message) {
        if (webhook != null) webhook.send(message);
    }

    public void send(File file) {
        if (webhook != null) webhook.send(file);
    }

    public void send(File file, String fileName) {
        if (webhook != null) webhook.send(file, fileName);
    }

    public void send(byte[] data, String fileName) {
        if (webhook != null) webhook.send(data, fileName);
    }

    public void send(InputStream data, String fileName) {
        if (webhook != null) webhook.send(data, fileName);
    }

    public void send(WebhookEmbed first, WebhookEmbed... embeds) {
        if (webhook != null) webhook.send(first, embeds);
    }

    public void send(Collection<WebhookEmbed> embeds) {
        if (webhook != null) webhook.send(embeds);
    }

    public void send(String content) {
        if (webhook != null) webhook.send(content);
    }
}
