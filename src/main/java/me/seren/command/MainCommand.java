package me.seren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.seren.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static me.seren.KingsWorld.modId;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MainCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("kingsworld")
            .requires(Utils.requirePermission("kings-world.main", 0))
            .executes(MainCommand::run)
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
        Utils.reloadModConfig(ctx.getSource().getServer());
        ctx.getSource().sendFeedback(Text.literal("The config has been reloaded"), true);
        return 1;
    }

    private static int send(CommandContext<ServerCommandSource> ctx) {
        String string = Utils.sendDiscordMessage(getString(ctx, "message"));
        ctx.getSource().sendFeedback(Text.literal(string), true);
        return 1;
    }

    private static int run(CommandContext<ServerCommandSource> ctx) {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modId);
        if (mod.isPresent()) {
            ModMetadata metadata = mod.get().getMetadata();
            String display = String.format("%s v%s", metadata.getName(), metadata.getVersion());
            ctx.getSource().sendFeedback(Text.literal(display), true);
            return 1;
        }

        ctx.getSource().sendError(Text.literal("Could not find the mod metadata"));
        return 0;
    }
}
