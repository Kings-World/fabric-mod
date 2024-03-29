package me.seren;

import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import okhttp3.OkHttpClient;

import static me.seren.KingsWorld.*;

public final class Events {
    public static void serverStarting(MinecraftServer server) {
        loadConfig();
        Utils.initializeWebhook();
        Utils.initializeClient(server);
    }

    public static void serverStarted(MinecraftServer server) {
        Utils.setStartedPresence();

        if (modConfig.getServerStartedMessage().isBlank()) return;
        Utils.sendDiscordMessage(modConfig.getServerStartedMessage());
    }

    public static void serverStopping(MinecraftServer server) {
        if (client == null) return;
        logger.info("Removing the JDA event listener");
        client.jda.removeEventListener(client.listener);

        if (!modConfig.getServerStoppedMessage().isBlank()) {
            logger.info("Sending the server stopped message");
            Utils.sendDiscordMessage(modConfig.getServerStoppedMessage());
        }

        if (!modConfig.getPersistCommands()) {
            logger.info("Deleting all slash commands");
            client.jda.updateCommands().queue();
        }
    }

    public static void serverStopped(MinecraftServer server) {
        if (webhook != null) {
            logger.info("Shutting down the webhook client");
            webhook.close();
        }

        if (client != null) {
            logger.info("Shutting down the JDA client");
            client.jda.shutdownNow();

            OkHttpClient httpClient = client.jda.getHttpClient();
            httpClient.connectionPool().evictAll();
            httpClient.dispatcher().executorService().shutdown();
        }
    }

    public static void chatMessage(FilteredMessage<SignedMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey) {
        if (modConfig.getChatMessage().isBlank()) return;
        Utils.sendEntityWebhook(sender, modConfig.getChatMessage()
            .replaceAll("\\{content}", message.raw().getContent().getString()));
    }

    public static void playerJoin(ServerPlayerEntity player) {
        if (modConfig.getPlayerJoinMessage().isBlank()) return;
        Utils.sendEntityWebhook(player, modConfig.getPlayerJoinMessage());
    }

    public static void playerLeave(ServerPlayerEntity player) {
        if (modConfig.getPlayerLeaveMessage().isBlank()) return;
        Utils.sendEntityWebhook(player, modConfig.getPlayerLeaveMessage());
    }

    public static void playerDeath(Entity entity, Text text) {
        if (!entity.isPlayer() || modConfig.getPlayerDeathMessage().isBlank()) return;
        Utils.sendEntityWebhook(entity, modConfig.getPlayerDeathMessage()
            .replaceAll("\\{message}", text.getString()));
    }

    public static void playerAdvancement(ServerPlayerEntity player, String title) {
        if (modConfig.getPlayerAdvancementMessage().isBlank()) return;
        Utils.sendEntityWebhook(player, modConfig.getPlayerAdvancementMessage()
            .replaceAll("\\{title}", title));
    }
}
