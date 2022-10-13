package me.seren.discord;

import me.seren.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;

import javax.security.auth.login.LoginException;

import static me.seren.KingsWorld.modConfig;

public class Client {
  public JDA jda;
  public MinecraftServer server;
  public Listener listener;

  public Client(MinecraftServer server) throws LoginException, InterruptedException {
    this.server = server;
    this.listener = new Listener(this);
    this.jda = JDABuilder
      .createDefault(modConfig.getDiscordToken())
      .addEventListeners(this.listener)
      .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
      .setActivity(Utils.activityOf(Utils.ActivityChoices.STARTING))
      .setStatus(modConfig.getStartingActivityStatus())
      .setEnableShutdownHook(false)
      .build()
      .awaitReady();
  }
}
