package me.seren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.seren.Events;
import me.seren.KingsWorld;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("discord")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("message", greedyString())
                        .executes(DiscordCommand::run))
        );
    }

    private static int run(CommandContext<ServerCommandSource> ctx) {
//        Events.getInstance().send(getString(ctx, "message"));
        KingsWorld.DiscordClient.sendMessage(getString(ctx, "message"));
        ctx.getSource().sendFeedback(Text.literal("The message has been sent"), true);
        return 1;
    }
}
