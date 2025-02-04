package net.kings_world.fabric_mod.events;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Optional;

import static net.kings_world.fabric_mod.Main.config;
import static net.kings_world.fabric_mod.Main.discord;

public class PlayerJoin {
    public static void run(ServerPlayerEntity player) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", player.getName().getString());
        placeholders.put("uuid", player.getUuidAsString());

        discord.sendWebhook(player, config.getMessages().playerJoin(), Optional.of(placeholders));
    }
}
