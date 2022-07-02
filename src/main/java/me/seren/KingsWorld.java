package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import com.mojang.brigadier.CommandDispatcher;
import de.maxhenkel.configbuilder.ConfigBuilder;
import me.seren.command.DiscordCommand;
import me.seren.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class KingsWorld implements ModInitializer {
	public static final String MOD_ID = "kings-world";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static WebhookClient Webhook;
	public static Config SERVER_CONFIG;

	@Override
	public void onInitialize() {
		// Path path = FabricLoader.getInstance().getConfigDir().resolve(Path.of(MOD_ID + ".properties"));
		Path path = new File("config/" + MOD_ID + ".properties").toPath();
		System.out.println(path.toAbsolutePath().toString());

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			SERVER_CONFIG = ConfigBuilder.build(path, true, Config::new);

			if (SERVER_CONFIG.isValidWebhookUrl()) {
				Webhook = WebhookClient.withUrl(SERVER_CONFIG.getWebhookUrl());
				Events.getInstance().startServer();
			} else {
				LOGGER.warn("Not able to parse a valid webhook URL");
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			Events.getInstance().stopServer();
		});

		registerCommands();

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, typeKey) ->
				Events.getInstance().playerMessage(sender, message.raw().getContent().getString())
		);

		LOGGER.info("Kings World has been finished loading");
	}

	private static void registerCommands() {
		registerCommand(DiscordCommand::register);
	}

	private static void registerCommand(Consumer<CommandDispatcher<ServerCommandSource>> cmd) {
		CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> cmd.accept(dispatcher));
	}
}
