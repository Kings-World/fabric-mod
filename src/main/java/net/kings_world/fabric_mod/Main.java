package net.kings_world.fabric_mod;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kings_world.fabric_mod.config.Config;
import net.kings_world.fabric_mod.discord.Discord;
import net.kings_world.fabric_mod.events.*;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main implements DedicatedServerModInitializer {
    public static final String modId = "kings-world";
    public static final Logger logger = LoggerFactory.getLogger(modId);
    public static final Path modConfigPath = FabricLoader.getInstance().getConfigDir().resolve(modId);

    public static MinecraftServer server;
    public static Config config;
    public static Discord discord;

    @Override
    public void onInitializeServer() {
        // server
        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting::run);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerStarted::run);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerStopping::run);

        // messages
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> ChatMessage.run(message, sender));
        Events.DISCORD_MESSAGE.register(DiscordMessage::run);

        // players
        Events.PLAYER_JOIN.register(PlayerJoin::run);
        Events.PLAYER_LEAVE.register(PlayerLeave::run);
        Events.PLAYER_DEATH.register(PlayerDeath::run);
        Events.PLAYER_ADVANCEMENT.register(PlayerAdvancement::run);

        // commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandRegistration.run(dispatcher));
    }
}
