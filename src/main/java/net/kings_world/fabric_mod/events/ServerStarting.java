package net.kings_world.fabric_mod.events;

import net.kings_world.fabric_mod.Main;
import net.kings_world.fabric_mod.config.Config;
import net.kings_world.fabric_mod.discord.Discord;
import net.minecraft.server.MinecraftServer;

import static net.kings_world.fabric_mod.Main.*;

public class ServerStarting {
    public static void run(MinecraftServer server) {
        logger.info("_  _ _ _  _ ____ ____    _ _ _ ____ ____ _    ___  ");
        logger.info("|_/  | |\\ | | __ [__     | | | |  | |__/ |    |  \\ ");
        logger.info("| \\_ | | \\| |__] ___]    |_|_| |__| |  \\ |___ |__/ ");
        logger.info("                                                   ");

        Main.server = server;
        config = new Config("config.yml");
        discord = new Discord(false);
    }
}
