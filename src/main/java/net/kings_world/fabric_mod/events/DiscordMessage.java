package net.kings_world.fabric_mod.events;

import net.dv8tion.jda.api.entities.Message;
import net.kings_world.fabric_mod.config.Config;
import net.kings_world.fabric_mod.discord.Formatters;
import net.minecraft.text.Text;

import java.util.HashMap;

import static net.kings_world.fabric_mod.Main.config;
import static net.kings_world.fabric_mod.Main.server;
import static net.kings_world.fabric_mod.Utils.stringReplace;

public class DiscordMessage {
    public static void run(Message message) {
        Config.DiscordMessage discordMessage = config.getMessages().discordMessage();
        Config.Message template = message.getReferencedMessage() != null
            ? discordMessage.reply()
            : discordMessage.standard();

        if (!template.enabled || template.content.isBlank()) return;
        if (message.getInteractionMetadata() != null) return;

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", Formatters.formatAuthor(message));
        placeholders.put("content", Formatters.formatMessage(message));
        placeholders.put("reference_name", Formatters.formatReference(message));

        server.getPlayerManager().broadcast(Text.of(stringReplace(template.content, placeholders)), false);
    }
}
