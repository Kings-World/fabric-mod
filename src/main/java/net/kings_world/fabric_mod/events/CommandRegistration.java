package net.kings_world.fabric_mod.events;

import com.mojang.brigadier.CommandDispatcher;
import net.kings_world.fabric_mod.commands.*;
import net.minecraft.server.command.ServerCommandSource;

import static net.kings_world.fabric_mod.Main.logger;
import static net.kings_world.fabric_mod.Main.modId;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegistration {
    public static void run(CommandDispatcher<ServerCommandSource> dispatcher) {
        logger.info("Registering all in-game commands under the '/{}' prefix", modId);

        dispatcher.register(
            literal(modId)
                .then(ReloadCommand.data)
                .then(SendCommand.data)
                .then(VersionCommand.data)
        );
    }
}
