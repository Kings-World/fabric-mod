package net.kings_world.fabric_mod.events;

import net.minecraft.server.MinecraftServer;

import static net.kings_world.fabric_mod.Main.*;

public class ServerStopping {
    public static void run(MinecraftServer server) {
        logger.info("Initializing shutdown sequence for the Discord bot");

        logger.info("Sending the shutdown message to Discord");
        discord.sendMessage(config.getMessages().stopped());
        logger.info("Discord message has been sent");

        logger.info("Closing the gateway connection");
        discord.shutdown();
        logger.info("Gateway connection has been closed");

        logger.info("Shutdown sequence has been completed");
    }
}
