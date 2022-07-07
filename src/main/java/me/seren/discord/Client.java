package me.seren.discord;

import me.seren.config.Config.ActivityTypes;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;

import javax.security.auth.login.LoginException;

import static me.seren.KingsWorld.config;

public class Client {
  public JDA jda;
  public MinecraftServer server;

  public Client(MinecraftServer server) throws LoginException, InterruptedException {
    this.server = server;
    this.jda = JDABuilder
      .createDefault(config.getDiscordToken())
      .addEventListeners(new Listener(this))
      .enableIntents(GatewayIntent.GUILD_MESSAGES)
      .setActivity(getActivityFromConfig())
      .setStatus(config.getClientStatus())
      .build()
      .awaitReady();
  }

  private Activity getActivityFromConfig() {
    if (config.getActivityType().equals(ActivityTypes.NONE)) return null;
    return Activity.of(
      ActivityType.valueOf(config.getActivityType().name()),
      config.getActivityName()
    );
  }
}
