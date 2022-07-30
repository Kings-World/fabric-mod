package me.seren.config;

import me.seren.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

public class ModConfig {
  public final Yaml yaml;
  private final YamlConfiguration config;

  public ModConfig(String file) {
    this.yaml = new Yaml(Utils.configFolder().resolve(file).toFile(), file);
    this.config = yaml.getConfig();
    yaml.load();
  }

  public void reloadFile() {
    yaml.load();
  }

  public String getWebhookUrl() {
    return config.getString("webhook_url", "");
  }

  public String getAvatarUrl() {
    return config.getString("avatar_url", "https://crafthead.net/helm/{uuid}");
  }

  public String getDiscordToken() {
    return config.getString("discord_token", "");
  }

  public String getChannelId() {
    return config.getString("channel_id", "");
  }

  public Boolean getPersistCommands() {
    return config.getBoolean("persist_commands", true);
  }

  // starting_activity

  public OnlineStatus getStartingActivityStatus() {
    return OnlineStatus.fromKey(config.getString("starting_activity.status", "IDLE"));
  }

  public Utils.ActivityTypes getStartingActivityType() {
    return Utils.parseActivityType(config.getString("starting_activity.type", "NONE"));
  }

  public String getStartingActivityName() {
    return config.getString("starting_activity.name", "Minecraft");
  }

  // started_activity

  public OnlineStatus getStartedActivityStatus() {
    return OnlineStatus.fromKey(config.getString("started_activity.status", "ONLINE"));
  }

  public Utils.ActivityTypes getStartedActivityType() {
    return Utils.parseActivityType(config.getString("started_activity.type", "NONE"));
  }

  public String getStartedActivityName() {
    return config.getString("started_activity.name", "Minecraft");
  }

  // messages

  public String getServerStartedMessage() {
    return config.getString("messages.server_started", ":white_check_mark: The server has started!");
  }

  public String getServerStoppedMessage() {
    return config.getString("messages.server_stopped", ":octagonal_sign: The server has stopped!");
  }

  public String getChatMessage() {
    return config.getString("messages.chat_message", "{name}: {content}");
  }

  public String getPlayerJoinMessage() {
    return config.getString("messages.player_join", ":arrow_right: {name} has joined!");
  }

  public String getPlayerLeaveMessage() {
    return config.getString("messages.player_leave", ":arrow_left: {name} has left!");
  }

  public String getPlayerDeathMessage() {
    return config.getString("messages.player_death", ":skull: {message}");
  }

  public String getPlayerAdvancementMessage() {
    return config.getString("messages.player_advancement", ":medal: {name} has completed the advancement **{title}**!");
  }
}
