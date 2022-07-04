package me.seren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.seren.Utils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(literal("discord")
      .requires(Permissions.require("kings-world.discord", 4))
      .then(argument("message", greedyString())
        .executes(DiscordCommand::run))
    );
  }

  private static int run(CommandContext<ServerCommandSource> ctx) {
    Utils.sendDiscordMessage(getString(ctx, "message"));
    ctx.getSource().sendFeedback(Text.literal("The message has been sent"), true);
    return 1;
  }
}
