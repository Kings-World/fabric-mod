package net.kings_world.fabric_mod.events;

import net.minecraft.server.MinecraftServer;


import static net.kings_world.fabric_mod.Main.*;

public class ServerStarted {
    public static void run(MinecraftServer server) {
        discord.sendMessage(config.getMessages().started());
        discord.setActivity(config.getStartedActivity());
    }
}
