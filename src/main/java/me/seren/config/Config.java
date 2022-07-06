package me.seren.config;

import club.minnced.discord.webhook.WebhookClientBuilder;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigEntry;
import net.dv8tion.jda.api.OnlineStatus;

import java.util.regex.Matcher;

public class Config {
  public final ConfigEntry<String> webhookUrl;
  public final ConfigEntry<String> avatarUrl;
  public final ConfigEntry<String> discordToken;
  public final ConfigEntry<String> channelId;
  public final ConfigEntry<Boolean> persistCommands;
  public final ConfigEntry<ActivityTypes> activityType;
  public final ConfigEntry<String> activityName;
  public final ConfigEntry<OnlineStatus> clientStatus;

  public Config(ConfigBuilder builder) {
    this.webhookUrl = builder.stringEntry("webhook_url", "");
    this.avatarUrl = builder.stringEntry("avatar_url", "https://crafthead.net/helm/{uuid}");
    this.discordToken = builder.stringEntry("discord_token", "");
    this.channelId = builder.stringEntry("channel_id", "");
    this.persistCommands = builder.booleanEntry("persist_commands", true);

    // activity
    this.clientStatus = builder.enumEntry("client_status", OnlineStatus.ONLINE);
    this.activityType = builder.enumEntry("activity_type", ActivityTypes.NONE);
    this.activityName = builder.stringEntry("activity_name", "Minecraft");
  }

  public String getWebhookUrl() {
    return webhookUrl.get();
  }

  public String getAvatarUrl() {
    return avatarUrl.get();
  }

  public String getDiscordToken() {
    return discordToken.get();
  }

  public String getChannelId() {
    return channelId.get();
  }

  public Boolean shouldPersistCommands() {
    return persistCommands.get();
  }

  public OnlineStatus getClientStatus() {
    return clientStatus.get();
  }

  public ActivityTypes getActivityType() {
    return activityType.get();
  }

  public String getActivityName() {
    return activityName.get();
  }

  public boolean isValidWebhookUrl() {
    Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(getWebhookUrl());
    return matcher.matches();
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
