package me.seren;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import static me.seren.KingsWorld.*;

public class Utils {
    public static void sendWebhook(WebhookMessage message) {
        if (WEBHOOK != null) WEBHOOK.send(message);
    }

    public static void sendWebhook(File file) {
        if (WEBHOOK != null) WEBHOOK.send(file);
    }

    public static void sendWebhook(File file, String fileName) {
        if (WEBHOOK != null) WEBHOOK.send(file, fileName);
    }

    public static void sendWebhook(byte[] data, String fileName) {
        if (WEBHOOK != null) WEBHOOK.send(data, fileName);
    }

    public static void sendWebhook(InputStream data, String fileName) {
        if (WEBHOOK != null) WEBHOOK.send(data, fileName);
    }

    public static void sendWebhook(WebhookEmbed first, WebhookEmbed... embeds) {
        if (WEBHOOK != null) WEBHOOK.send(first, embeds);
    }

    public static void sendWebhook(Collection<WebhookEmbed> embeds) {
        if (WEBHOOK != null) WEBHOOK.send(embeds);
    }

    public static void sendWebhook(String content) {
        if (WEBHOOK != null) WEBHOOK.send(content);
    }
}
