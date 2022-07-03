package me.seren.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
      .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
      .build()
      .awaitReady();
  }

  public void addAndUpdateCommands(JDA jda) {
    jda.updateCommands().addCommands(
      Commands.slash("list", "View a list of online players")
    ).queue();
  }
}
