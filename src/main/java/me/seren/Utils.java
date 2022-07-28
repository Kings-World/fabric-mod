package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.seren.discord.Client;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.Presence;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static me.seren.KingsWorld.*;

public class Utils {
  public static Collection<Message.MentionType> jdaMentions = Arrays.asList(
    Message.MentionType.CHANNEL,
    Message.MentionType.EMOJI
  );

  public static void printBanner() {
    logger.info("_  _ _ _  _ ____ ____    _ _ _ ____ ____ _    ___  ");
    logger.info("|_/  | |\\ | | __ [__     | | | |  | |__/ |    |  \\ ");
    logger.info("| \\_ | | \\| |__] ___]    |_|_| |__| |  \\ |___ |__/ ");
    logger.info("                                                   ");
  }

  public static String sendDiscordMessage(CharSequence text) {
    if (client == null) return "The discord client is not initialized";

    TextChannel channel = client.jda.getTextChannelById(modConfig.getChannelId());
    if (channel == null) {
      logger.warn("I'm unable to find a channel with the id \"" + modConfig.getChannelId() + "\"");
      return "I'm unable to find a channel with the id \"" + modConfig.getChannelId() + "\"";
    } else if (!channel.canTalk()) {
      logger.warn("I'm unable to talk in the channel #" + channel.getName());
      return "I'm unable to talk in the channel #" + channel.getName();
    } else {
      channel.sendMessage(text).allowedMentions(jdaMentions).queue();
      return "The message has been sent to #" + channel.getName();
    }
  }

  public static void sendEntityWebhook(Entity entity, String content) {
    if (webhook == null) return;

    webhook.send(new WebhookMessageBuilder()
      .setContent(getEntityValues(content, entity))
      .setUsername(entity.getEntityName())
      .setAvatarUrl(getEntityAvatar(entity))
      .setAllowedMentions(AllowedMentions.none())
      .build());
  }

  public static void initializeWebhook() {
    logger.info("Creating webhook client...");
    try {
      webhook = WebhookClient.withUrl(modConfig.getWebhookUrl());
    } catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
    }
  }

  public static void initializeClient(MinecraftServer server) {
    logger.info("Creating discord client...");
    try {
      client = new Client(server);
    } catch (LoginException | InterruptedException e) {
      logger.error(e.getMessage());
    }
  }

  public static void setStartedPresence() {
    if (client == null) return;
    Presence presence = client.jda.getPresence();
    presence.setActivity(Utils.activityOf(Utils.ActivityChoices.STARTED));
    presence.setStatus(modConfig.getStartedActivityStatus());
  }

  public static void reloadModConfig(MinecraftServer server) {
    modConfig.reloadFile();

    if (webhook != null) webhook.close();
    initializeWebhook();

    if (client != null) client.jda.shutdown();
    initializeClient(server);
    setStartedPresence();
  }

  public static Predicate<ServerCommandSource> requirePermission(String permission, int defaultLevel) {
    if (isFabricPermissionsAPILoaded()) return Permissions.require(permission, defaultLevel);
    return source -> source.hasPermissionLevel(defaultLevel);
  }

  public static boolean isFabricPermissionsAPILoaded() {
    return FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
  }

  public static String getEntityAvatar(Entity entity) {
    String url = modConfig.getAvatarUrl();
    return Utils.getEntityValues(url, entity);
  }

  public static String getEntityValues(String string, Entity entity) {
    return string
      .replaceAll("\\{uuid\\}", entity.getUuidAsString())
      .replaceAll("\\{name\\}", entity.getEntityName());
  }

  public static Path configFolder() {
    return FabricLoader.getInstance().getConfigDir().resolve(modId.toLowerCase());
  }

  public static ActivityTypes parseActivityType(String activity) {
    return switch (activity.toUpperCase()) {
      case "PLAYING" -> ActivityTypes.PLAYING;
      case "STREAMING" -> ActivityTypes.STREAMING;
      case "LISTENING" -> ActivityTypes.LISTENING;
      case "WATCHING" -> ActivityTypes.WATCHING;
      case "CUSTOM_STATUS" -> ActivityTypes.CUSTOM_STATUS;
      case "COMPETING" -> ActivityTypes.COMPETING;
      default -> ActivityTypes.NONE;
    };
  }

  public static Activity activityOf(ActivityChoices activity) {
    ActivityTypes type = activity == ActivityChoices.STARTING
      ? modConfig.getStartingActivityType()
      : modConfig.getStartedActivityType();
    String name = activity == ActivityChoices.STARTING
      ? modConfig.getStartingActivityName()
      : modConfig.getStartedActivityName();

    if (type.equals(ActivityTypes.NONE)) return null;
    return Activity.of(Activity.ActivityType.valueOf(type.name()), name);
  }

  public enum ActivityChoices {
    STARTING,
    STARTED
  }

  public enum ActivityTypes {
    PLAYING,
    STREAMING,
    LISTENING,
    WATCHING,
    CUSTOM_STATUS,
    COMPETING,
    NONE,
  }
}
