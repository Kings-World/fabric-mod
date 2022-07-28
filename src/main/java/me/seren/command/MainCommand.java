package me.seren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.seren.Utils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static me.seren.KingsWorld.modConfig;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MainCommand {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(literal("kingsworld")
      .requires(Utils.requirePermission("kings-world.main", 0))
      // reload command
      .then(literal("reload")
        .requires(Utils.requirePermission("kings-world.reload", 4))
        .executes(MainCommand::reload))
      // send command
      .then(literal("send")
        .requires(Utils.requirePermission("kings-world.send", 4))
        .then(argument("message", greedyString())
          .executes(MainCommand::send)))
    );
  }

  private static int reload(CommandContext<ServerCommandSource> ctx) {
    modConfig.reload();
    ctx.getSource().sendFeedback(Text.literal("The config has been reloaded"), true);
    return 1;
  }

  private static int send(CommandContext<ServerCommandSource> ctx) {
    String string = Utils.sendDiscordMessage(getString(ctx, "message"));
    ctx.getSource().sendFeedback(Text.literal(string), true);
    return 1;
  }
}
