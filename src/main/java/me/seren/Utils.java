package me.seren;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.dv8tion.jda.api.entities.TextChannel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Predicate;

import static me.seren.KingsWorld.*;

public class Utils {
  public static void sendDiscordMessage(CharSequence text) {
    if (client == null) {
      logger.error("""
        Couldn't send discord message because the client is null
        Please check you have provided a valid token in the config
        """);
      return;
    }

    TextChannel channel = client.jda.getTextChannelById(config.getChannelId());
    if (channel == null) logger.warn("I'm unable to find a channel with the id \"" + config.getChannelId() + "\"");
    else if (!channel.canTalk()) logger.warn("I'm unable to talk in the channel #" + channel.getName());
    else channel.sendMessage(text).queue();
  }

  public static void sendPlayerWebhook(ServerPlayerEntity player, String content) {
    if (webhook == null) {
      logger.error("""
        Couldn't send webhook message because the webhook client is null
        Please check you have provided a valid webhook in the config
        """);
      return;
    }

    webhook.send(new WebhookMessageBuilder()
      .setContent(content
        .replaceAll("\\$name", player.getEntityName()))
      .setUsername(player.getEntityName())
      .setAvatarUrl(config.getPlayerAvatar(player))
      .build());
  }

  public static Predicate<ServerCommandSource> requirePermission(String permission, int defaultLevel) {
    if (isFabricPermissionsAPILoaded()) return Permissions.require(permission, defaultLevel);
    return source -> source.hasPermissionLevel(defaultLevel);
  }

  public static boolean isFabricPermissionsAPILoaded() {
    boolean loaded = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
    if (loaded) logger.info("Kings World: Using Fabric Permissions API");
    return loaded;
  }
}
