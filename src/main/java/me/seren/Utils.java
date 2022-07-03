package me.seren;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import static me.seren.KingsWorld.*;

public class Utils {
  public static void sendDiscordMessage(CharSequence text) {
    TextChannel channel = client.jda.getTextChannelById(config.getChannelId());
    if (channel == null) logger.warn("I'm unable to find a channel with the id \"" + config.getChannelId() + "\"");
    else if (!channel.canTalk()) logger.warn("I'm unable to talk in the channel #" + channel.getName());
    else channel.sendMessage(text).queue();
  }

  public static void sendPlayerWebhook(ServerPlayerEntity player, String content) {
    content = content
      .replaceAll("\\$name", player.getEntityName());

    if (webhook == null) return;
    webhook.send(new WebhookMessageBuilder()
      .setContent(content)
      .setUsername(player.getEntityName())
      .setAvatarUrl(config.getPlayerAvatar(player))
      .build());
  }

  public static void sendWebhookMessage(WebhookMessage message) {
    if (webhook != null) webhook.send(message);
  }

  public static void sendWebhookMessage(File file) {
    if (webhook != null) webhook.send(file);
  }

  public static void sendWebhookMessage(File file, String fileName) {
    if (webhook != null) webhook.send(file, fileName);
  }

  public static void sendWebhookMessage(byte[] data, String fileName) {
    if (webhook != null) webhook.send(data, fileName);
  }

  public static void sendWebhookMessage(InputStream data, String fileName) {
    if (webhook != null) webhook.send(data, fileName);
  }

  public static void sendWebhookMessage(WebhookEmbed first, WebhookEmbed... embeds) {
    if (webhook != null) webhook.send(first, embeds);
  }

  public static void sendWebhookMessage(Collection<WebhookEmbed> embeds) {
    if (webhook != null) webhook.send(embeds);
  }

  public static void sendWebhookMessage(String content) {
    if (webhook != null) webhook.send(content);
  }
}
