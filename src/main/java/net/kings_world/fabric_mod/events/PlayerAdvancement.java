package net.kings_world.fabric_mod.events;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Optional;

import static net.kings_world.fabric_mod.Main.config;
import static net.kings_world.fabric_mod.Main.discord;

public class PlayerAdvancement {
    public static void run(ServerPlayerEntity player, AdvancementDisplay advancement) {
        if (advancement.getTitle().getString().isBlank()) return;

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", player.getName().getString());
        placeholders.put("uuid", player.getUuidAsString());
        placeholders.put("title", advancement.getTitle().getString());
        placeholders.put("description", advancement.getDescription().getString());

        discord.sendWebhook(player, config.getMessages().playerAdvancement(), Optional.of(placeholders));
    }
}
