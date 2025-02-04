package net.kings_world.fabric_mod.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kings_world.fabric_mod.config.Config;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

import static net.kings_world.fabric_mod.Main.*;
import static net.kings_world.fabric_mod.Main.discord;
import static net.kings_world.fabric_mod.Utils.stringReplace;

public class Discord {
    public JDA jda;
    public Webhook webhook;

    public static Collection<Message.MentionType> allowedMentionTypes = List.of(
        Message.MentionType.SLASH_COMMAND,
        Message.MentionType.CHANNEL,
        Message.MentionType.EMOJI
    );

    public Discord(Boolean reloaded) {
        if (config.getDiscordToken().isBlank()) {
            logger.warn("The Discord token is not set - please set it in the config file.");
            return;
        }

        logger.info("Initializing the Discord bot...");

        Config.Activity startingActivity = reloaded ? config.getStartedActivity() : config.getStartingActivity();

        if (reloaded) {
            logger.info("Reloading with the following activity: {} {} and the status: {}", startingActivity.type(), startingActivity.name(), startingActivity.status());
        } else {
            logger.info("Starting with the following activity: {} {} and the status: {}", startingActivity.type(), startingActivity.name(), startingActivity.status());
        }

       JDABuilder builder = JDABuilder.createLight(config.getDiscordToken(), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
           .addEventListeners(new Listener(this))
           .setStatus(startingActivity.status());

       if (startingActivity.type() != Config.ActivityTypeChoice.NONE) {
           builder.setActivity(Activity.of(
               Activity.ActivityType.fromKey(startingActivity.type().ordinal()),
               startingActivity.name()
           ));
       } else {
           logger.warn("The activity type is set to null - skipping activity.");
       }

       try {
           jda = builder.build();
       } catch (Exception exception) {
           logger.error("Failed to initialize Discord bot.", exception);
       }

        logger.info("Logging into Discord...");
        try {
            jda.awaitReady();
        } catch (InterruptedException exception) {
            logger.info("Failed to login to Discord.", exception);
        }
    }

    public void setActivity(Config.Activity activity) {
        if (jda == null) {
            logger.warn("Cannot set the activity because the bot is not logged in.");
            return;
        }

        logger.info("Setting the presence to {} {} and the status to {}", activity.type(), activity.name(), activity.status());

        jda.getPresence().setPresence(activity.status(), Activity.of(
            Activity.ActivityType.fromKey(activity.type().ordinal()),
            activity.name()
        ));
    }

    public void retrieveWebhook() {
        if (jda == null) {
            logger.warn("Cannot retrieve the webhook because the bot is not logged in.");
            return;
        }

        Config.WebhookData webhookData = config.getWebhookData();
        webhook = jda.retrieveWebhookById(webhookData.id()).complete();
    }

    public void sendMessage(Config.Message message) {
        if (!message.enabled || message.content.isBlank()) return;
        sendChannelMessage(message.content);
    }

    public void sendChannelMessage(CharSequence text) {
        if (jda == null) {
            logger.warn("Cannot send a message because the bot is not logged in.");
            return;
        }

        TextChannel channel = jda.getTextChannelById(config.getChannelId());
        if (channel == null) {
            logger.warn("The provided channel ID is invalid.");
            return;
        }

        if (!channel.canTalk()) {
            logger.warn("The bot does not have permission to send messages in the provided channel.");
            return;
        }

        channel.sendMessage(text).setAllowedMentions(allowedMentionTypes).queue();
    }

    public void sendWebhook(ServerPlayerEntity player, Config.Message message, Optional<Map<String, String>> replacements) {
        if (!message.enabled || message.content.isBlank()) return;
        replacements.ifPresentOrElse(
            r -> sendWebhookMessage(player, stringReplace(message.content, r)),
            () -> sendWebhookMessage(player, message.content)
        );
    }

    public void sendWebhookMessage(ServerPlayerEntity player, String content) {
        if (webhook == null) {
            logger.warn("Cannot send a message because the webhook is not set.");
            return;
        }

        webhook.sendMessage(content)
            .setUsername(player.getName().toString())
            .setAvatarUrl(config.getAvatarUrl().replace("{uuid}", player.getUuidAsString()))
            .setAllowedMentions(allowedMentionTypes)
            .queue();
    }

    public void shutdown() {
        if (jda == null) {
            logger.warn("Cannot shut down the bot because it is not logged in.");
            return;
        }

        logger.info("Shutting down the Discord bot...");
        try {
            discord.jda.shutdown();
            discord.jda.awaitShutdown();
        } catch (InterruptedException e) {
            logger.error("An error occurred while shutting down the Discord bot", e);
        }
    }
}
