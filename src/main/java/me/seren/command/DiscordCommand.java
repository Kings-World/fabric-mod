package me.seren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager
                .literal("discord")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(DiscordCommand::run)
        );
    }

    private static int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException  {
        ctx.getSource().sendFeedback(Text.literal("This command is not ready"), true);
        return 1;
    }
}
