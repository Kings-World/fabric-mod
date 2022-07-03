package me.seren;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.server.network.ServerPlayerEntity;

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
}
