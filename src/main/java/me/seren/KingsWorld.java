package me.seren;

import club.minnced.discord.webhook.WebhookClient;
import me.seren.command.MainCommand;
import me.seren.config.ModConfig;
import me.seren.discord.Client;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KingsWorld implements ModInitializer {
  public static final String modId = "kings-world";
  public static final Logger logger = LoggerFactory.getLogger(modId);
  public static WebhookClient webhook;
  public static Client client;
  public static ModConfig modConfig;

  @Override
  public void onInitialize() {
    registerEvents();
    registerCommands();
  }

  public static void loadConfig() {
    Utils.printBanner();
    modConfig = new ModConfig("config.yml");
    if (Utils.isFabricPermissionsAPILoaded()) {
      logger.info("Loaded Fabric Permissions API");
    }
  }

  private void registerEvents() {
    // fabric-lifecycle-events-v1
    ServerLifecycleEvents.SERVER_STARTING.register(Events::serverStarting);
    ServerLifecycleEvents.SERVER_STARTED.register(Events::serverStarted);
    ServerLifecycleEvents.SERVER_STOPPING.register(Events::serverStopping);

    // fabric-message-api-v1
    ServerMessageEvents.CHAT_MESSAGE.register(Events::chatMessage);
  }

  private void registerCommands() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> MainCommand.register(dispatcher));
  }
}
