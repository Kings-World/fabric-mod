package net.kings_world.fabric_mod.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static net.kings_world.fabric_mod.Main.logger;
import static net.kings_world.fabric_mod.Main.modId;
import static net.kings_world.fabric_mod.Utils.requirePermission;
import static net.minecraft.server.command.CommandManager.*;

public class VersionCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> data = literal("version")
        .requires(requirePermission("version", 0))
        .executes(context -> {
            Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);

            if (modContainer.isPresent()) {
                ModMetadata metadata = modContainer.get().getMetadata();
                context.getSource().sendFeedback(
                    () -> Text.literal("%s v%s".formatted(metadata.getName(), metadata.getVersion().getFriendlyString())),
                    false);
                return 1;
            }

            context.getSource().sendError(Text.of("The mod is not installed."));
            return 0;
        });
}
