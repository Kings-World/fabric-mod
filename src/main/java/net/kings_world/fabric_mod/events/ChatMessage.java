package net.kings_world.fabric_mod.events;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Optional;

import static net.kings_world.fabric_mod.Main.config;
import static net.kings_world.fabric_mod.Main.discord;

public class ChatMessage {
    public static void run(SignedMessage message, ServerPlayerEntity player) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", player.getName().getString());
        placeholders.put("uuid", player.getUuidAsString());
        placeholders.put("content", message.getContent().toString());

        discord.sendWebhook(player, config.getMessages().chatMessage(), Optional.of(placeholders));
    }
}
