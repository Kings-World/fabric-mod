package net.kings_world.fabric_mod.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.kings_world.fabric_mod.Main.discord;
import static net.kings_world.fabric_mod.Utils.requirePermission;
import static net.minecraft.server.command.CommandManager.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class SendCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> data = literal("send")
        .requires(requirePermission("send", 4))
        .then(argument("message", greedyString()).executes(context -> {
            context.getSource().sendFeedback(() -> Text.of("Sending your message to Discord"), true);
            discord.sendChannelMessage(getString(context, "message"));
            return 1;
        }));
}
