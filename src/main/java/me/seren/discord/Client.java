package me.seren.discord;

import me.seren.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.minecraft.server.MinecraftServer;

import javax.security.auth.login.LoginException;

import static me.seren.KingsWorld.*;

public class Client {
    public JDA jda;
    public MinecraftServer server;

    public Client(MinecraftServer server) throws LoginException, InterruptedException {
        this.server = server;
        this.jda = JDABuilder
                .createDefault(CONFIG.getDiscordToken())
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
        TextChannel channel = this.jda.getChannelById(TextChannel.class, CONFIG.getChannelId());
        if (channel == null) LOGGER.warn("I'm unable to find a channel with the id \"" + CONFIG.getChannelId() + "\"");
        else if (!channel.canTalk()) LOGGER.warn("I'm unable to talk in the channel #" + channel.getName());
        else channel.sendMessage(text).queue();
    }

    public void addAndUpdateCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("list", "View a list of online players")
        ).queue();
    }
}
