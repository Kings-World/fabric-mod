package net.kings_world.fabric_mod.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kings_world.fabric_mod.config.Config;
import net.kings_world.fabric_mod.discord.Discord;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.kings_world.fabric_mod.Main.*;
import static net.kings_world.fabric_mod.Utils.requirePermission;
import static net.minecraft.server.command.CommandManager.*;

public class ReloadCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> data = literal("reload")
        .requires(requirePermission("reload", 4))
        .executes(context -> {
            send(context, "Reloading the config, please wait...");
            Config.ConfigChanges configChanges = config.reload();

            if (configChanges.webhookUrl()) {
                send(context, "The webhook URL has been changed.");
                config.parseWebhookUrl();
            }

            if (configChanges.discordToken()) {
                send(context, "The Discord token has been changed.");

                if (discord != null && discord.jda != null) {
                    discord.shutdown();
                }

                if (config.getDiscordToken().isBlank()) {
                    discord.jda = null;
                } else {
                    discord = new Discord(true);
                }
            }

            if (configChanges.presence()) {
                send(context, "The presence has been changed.");
                discord.setActivity(config.getStartedActivity());
            }

            send(context, "The config has reload successfully!");
            return 1;
        });

    private static void send(CommandContext<ServerCommandSource> context, String message) {
        context.getSource().sendFeedback(() -> Text.of(message), true);
    }
}
