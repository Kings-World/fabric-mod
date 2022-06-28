package me.seren.config;

import club.minnced.discord.webhook.WebhookClientBuilder;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.regex.Matcher;

public class Config {
    public final ConfigEntry<String> webhookUrl;
    public final ConfigEntry<String> avatarUrl;

    public Config(ConfigBuilder builder) {
        this.webhookUrl = builder.stringEntry("webhook_url", "");
        this.avatarUrl = builder.stringEntry("avatar_url", "https://crafthead.net/helm/{uuid}");
    }

    public String getWebhookUrl() {
        return webhookUrl.get();
    }

    public String getAvatarUrl() {
        return avatarUrl.get();
    }

    public boolean isValidWebhookUrl() {
        Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(getWebhookUrl());
        return matcher.matches();
    }

    public String getPlayerAvatar(ServerPlayerEntity player) {
        String url = getAvatarUrl();
        if (url.isEmpty() || url.isBlank()) url = avatarUrl.getDefault();
        return url.replace("{uuid}", player.getUuid().toString()).replace("{name}", player.getName().toString());
    }
}
