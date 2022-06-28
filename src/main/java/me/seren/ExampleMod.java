package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import de.maxhenkel.configbuilder.ConfigBuilder;
import me.seren.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "kings-world";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static WebhookClient Webhook;

	public static Config SERVER_CONFIG;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			SERVER_CONFIG = ConfigBuilder.build(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".properties"), true, Config::new);

			if (SERVER_CONFIG.isValidWebhookUrl()) {
				Webhook = WebhookClient.withUrl(SERVER_CONFIG.getWebhookUrl());
				Webhook.send(":white_check_mark: The server has started!");
			} else {
				LOGGER.warn("Not able to parse a valid webhook URL");
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			if (Webhook != null) {
				Webhook.send(":octagonal_sign: The server has stopped!");
			}
		});



		LOGGER.info("Kings World has been finished loading");
	}


}
