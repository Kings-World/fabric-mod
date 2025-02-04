package net.kings_world.fabric_mod.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Optional;

import static net.kings_world.fabric_mod.Main.config;
import static net.kings_world.fabric_mod.Main.discord;

public class PlayerLeave {
    public static void run(ServerPlayerEntity player, Text reason) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", player.getName().getString());
        placeholders.put("uuid", player.getUuidAsString());
        placeholders.put("reason", reason.getString());

        discord.sendWebhook(player, config.getMessages().playerLeave(), Optional.of(placeholders));
    }
}
