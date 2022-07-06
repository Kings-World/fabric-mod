package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import me.seren.discord.Client;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

import javax.security.auth.login.LoginException;

import static me.seren.KingsWorld.*;

public final class Events {
  public static void serverStarting(MinecraftServer server) {
    if (config.isValidWebhookUrl()) {
      webhook = WebhookClient.withUrl(config.getWebhookUrl());
    } else {
      logger.error("Config[Webhook]: Failed to parse webhook URL");
    }

    try {
      client = new Client(server);
    } catch (LoginException | InterruptedException e) {
      logger.error("Config[Discord]: " + e.getLocalizedMessage());
    }
  }

  public static void serverStarted(MinecraftServer server) {
    Utils.sendDiscordMessage(":white_check_mark: The server has started!");
  }

  public static void serverStopped(MinecraftServer server) {
    Utils.sendDiscordMessage(":octagonal_sign: The server has stopped!");
  }

  public static void chatMessage(FilteredMessage<SignedMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey) {
    Utils.sendEntityWebhook(sender, "{name}: " + message.raw().getContent().getString());
  }

  public static void playerJoin(ServerPlayerEntity player) {
    Utils.sendEntityWebhook(player, ":arrow_right: {name} has joined!");
  }

  public static void playerLeave(ServerPlayerEntity player) {
    Utils.sendEntityWebhook(player, ":arrow_left: {name} has left!");
  }

  public static void playerDeath(Entity entity, Text text) {
    Utils.sendEntityWebhook(entity, ":skull: " + text.getString());
  }
}
