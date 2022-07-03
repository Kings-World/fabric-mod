package me.seren.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;

import javax.security.auth.login.LoginException;

import static me.seren.KingsWorld.config;
import static me.seren.KingsWorld.logger;

public class Client {
  public JDA jda;
  public MinecraftServer server;

  public Client(MinecraftServer server) throws LoginException, InterruptedException {
    this.server = server;
    this.jda = JDABuilder
      .createDefault(config.getDiscordToken())
      .addEventListeners(new Listener(this))
      .enableIntents(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_WEBHOOKS
      )
      .build()
      .awaitReady();
  }

  public void sendMessage(CharSequence text) {
    TextChannel channel = this.jda.getChannelById(TextChannel.class, config.getChannelId());
    if (channel == null) logger.warn("I'm unable to find a channel with the id \"" + config.getChannelId() + "\"");
    else if (!channel.canTalk()) logger.warn("I'm unable to talk in the channel #" + channel.getName());
    else channel.sendMessage(text).queue();
  }

  public void addAndUpdateCommands(JDA jda) {
    jda.updateCommands().addCommands(
      Commands.slash("list", "View a list of online players")
    ).queue();
  }
}
