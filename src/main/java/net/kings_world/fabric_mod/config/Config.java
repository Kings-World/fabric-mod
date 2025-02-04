package net.kings_world.fabric_mod.config;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Webhook;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.util.regex.Matcher;

import static net.kings_world.fabric_mod.Main.logger;
import static net.kings_world.fabric_mod.Main.modConfigPath;

public class Config {
    public final Yaml yaml;
    private final YamlConfiguration config;

    private String webhookId = null;
    private String webhookToken = null;

    public Config(String file) {
        this.yaml = new Yaml(modConfigPath.resolve(file).toFile(), file);
        this.config = yaml.getConfig();
        load();
    }

    public String getWebhookUrl() {
        return config.getString("webhook_url", "");
    }

    public WebhookData getWebhookData() {
        return new WebhookData(webhookId, webhookToken);
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

    public Activity getStartingActivity() {
        return new Activity(
            parseActivityStatus(config.getString("starting_activity.status", "ONLINE")),
            parseActivityType(config.getString("starting_activity.type", "PLAYING")),
            config.getString("starting_activity.name", "Minecraft")
        );
    }

    public Activity getStartedActivity() {
        return new Activity(
            parseActivityStatus(config.getString("started_activity.status", "ONLINE")),
            parseActivityType(config.getString("started_activity.type", "PLAYING")),
            config.getString("started_activity.name", "Minecraft")
        );
    }

    public Messages getMessages() {
        return new Messages(
            new Message("server_started", ":white_check_mark: The server has started!"),
            new Message("server_stopped", ":octagonal_sign: The server has stopped!"),
            new Message("chat_message", "{name}: {content}"),
            new Message("player_join", ":arrow_right: {name} has joined!"),
            new Message("player_leave", ":arrow_left: {name} has left!"),
            new Message("player_death", ":skull: {message}"),
            new Message("player_advancement", ":medal: {name} has completed the advancement **{title}**!"),
            new DiscordMessage(
                new Message("discord_message.standard", "{name}: {content}"),
                new Message("discord_message.reply", "{name} -> {reference_name}: {content}")
            )
        );
    }

    public void load() {
        logger.info("Loading the config file");
        yaml.load();
        parseWebhookUrl();
    }

    public ConfigChanges reload() {
        logger.info("Reloading the config file");

        String oldDiscordToken = getDiscordToken();
        String oldWebhookUrl = getWebhookUrl();
        Activity oldPresence = getStartedActivity();

        yaml.load();

        return new ConfigChanges(
            !oldDiscordToken.equals(getDiscordToken()),
            !oldWebhookUrl.equals(getWebhookUrl()),
            !oldPresence.name.equals(getStartedActivity().name) ||
                oldPresence.status != getStartedActivity().status ||
                oldPresence.type != getStartedActivity().type
        );
    }

    public void parseWebhookUrl() {
        if (getWebhookUrl().isBlank()) {
            logger.warn("The Webhook URL is not set - please set it in the config file.");
            return;
        }

        logger.info("Attempting to parse webhook URL");
        Matcher matcher = Webhook.WEBHOOK_URL.matcher(getWebhookUrl());

        if (!matcher.matches()) {
            logger.warn("Failed to parse the given webhook URL! Please check the URL and try again.");
            // setting back to null so if a user runs the reload command, it will reset rather than keep the old id/token
            webhookId = null;
            webhookToken = null;
            return;
        }

        webhookId = matcher.group(1);
        webhookToken = matcher.group(2);
        logger.info("Webhook URL has been parsed successfully");
    }

    private OnlineStatus parseActivityStatus(String status) {
        return switch (status.toUpperCase()) {
            case "IDLE" -> OnlineStatus.IDLE;
            case "DND", "DO_NOT_DISTURB" -> OnlineStatus.DO_NOT_DISTURB;
            case "INVISIBLE", "OFFLINE" -> OnlineStatus.INVISIBLE;
            default -> OnlineStatus.ONLINE;
        };
    }

    private ActivityTypeChoice parseActivityType(String type) {
        return switch (type.toUpperCase()) {
            case "PLAYING" -> ActivityTypeChoice.PLAYING;
            case "STREAMING" -> ActivityTypeChoice.STREAMING;
            case "LISTENING" -> ActivityTypeChoice.LISTENING;
            case "WATCHING" -> ActivityTypeChoice.WATCHING;
            case "COMPETING" -> ActivityTypeChoice.COMPETING;
            case "CUSTOM_STATUS", "CUSTOM" -> ActivityTypeChoice.CUSTOM_STATUS;
            default -> ActivityTypeChoice.NONE;
        };
    }

    public enum ActivityTypeChoice {
        PLAYING,
        STREAMING,
        LISTENING,
        WATCHING,
        CUSTOM_STATUS,
        COMPETING,
        NONE,
    }

    public record WebhookData(String id, String token) {}

    public record Activity(OnlineStatus status, ActivityTypeChoice type, String name) {}

    public record Messages(
        Message started,
        Message stopped,
        Message chatMessage,
        Message playerJoin,
        Message playerLeave,
        Message playerDeath,
        Message playerAdvancement,
        DiscordMessage discordMessage
    ) {}

    public record DiscordMessage(Message standard, Message reply) {}

    public record ConfigChanges(Boolean discordToken, Boolean webhookUrl, Boolean presence) {}

    public class Message {
        public final String content;
        public final Boolean enabled;
        public final String id;

        public Message(String id, String def) {
            this.id = id;
            this.content = config.getString("messages." + id + ".content", def);
            this.enabled = config.getBoolean("messages." + id + ".enabled", true);
        }

        public String toString() {
            return content;
        }
    }
}
