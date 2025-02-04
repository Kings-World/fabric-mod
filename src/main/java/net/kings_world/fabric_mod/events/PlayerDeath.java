package net.kings_world.fabric_mod.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Optional;

import static net.kings_world.fabric_mod.Main.config;
import static net.kings_world.fabric_mod.Main.discord;

public class PlayerDeath {
    public static void run(ServerPlayerEntity player, Text message) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", player.getName().getString());
        placeholders.put("uuid", player.getUuidAsString());
        placeholders.put("message", message.toString());

        discord.sendWebhook(player, config.getMessages().playerDeath(), Optional.of(placeholders));
    }
}
