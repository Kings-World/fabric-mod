package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import de.maxhenkel.configbuilder.ConfigBuilder;
import me.seren.command.DiscordCommand;
import me.seren.config.Config;
import me.seren.discord.Client;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class KingsWorld implements ModInitializer {
	public static final String MOD_ID = "kings-world";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static WebhookClient WEBHOOK;
	public static Client CLIENT;
	public static Config CONFIG;

	@Override
	public void onInitialize() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(Path.of(MOD_ID + ".properties"));
		CONFIG = ConfigBuilder.build(path, true, Config::new);

		registerEvents();
		registerCommands();
	}

	private static void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTING.register(Events::serverStarting);
		ServerLifecycleEvents.SERVER_STARTED.register(Events::serverStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(Events::serverStopped);
		ServerMessageEvents.CHAT_MESSAGE.register(Events::chatMessage);
	}

	private static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> DiscordCommand.register(dispatcher));
	}
}
