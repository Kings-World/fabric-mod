package net.kings_world.fabric_mod.discord;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.kings_world.fabric_mod.Events;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kings_world.fabric_mod.Main.*;

public class Listener extends ListenerAdapter {
    Discord discord;

    public Listener(Discord discord) {
        this.discord = discord;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Deploying slash commands");
        event.getJDA().updateCommands().addCommands(
            Commands.slash("list", "List all players on the server"),
            Commands.slash("message", "Send a message to a player on the server")
                .addOption(OptionType.STRING, "player", "The player to send the message to", true, true)
                .addOption(OptionType.STRING, "content", "The content of the message", true)
        ).queue();
        logger.info("Slash commands deployed");

        logger.info("Retrieving the webhook...");
        discord.retrieveWebhook();
        logger.info("Webhook retrieved");

        logger.info("Logged into the Discord gateway as {}", event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (
            event.getAuthor().isBot() ||
                !event.isFromGuild() ||
                !event.getChannel().getId().equals(config.getChannelId()) ||
                (event.isWebhookMessage() && Objects.requireNonNull(event.getMessage().getInteractionMetadata()).getId().equals(config.getWebhookData().id()))
        ) return;

        Events.DISCORD_MESSAGE.invoker().onDiscordMessage(event.getMessage());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        logger.info("Executing the /{} command from {}", event.getName(), event.getUser().getEffectiveName());

        switch (event.getName()) {
            case "list" -> listCommand(event);
            case "message" -> messageCommand(event);
            default -> event.reply("The requested command was not found!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("message") && event.getFocusedOption().getName().equals("player")) {
            Stream<Command.Choice> list = server.getPlayerManager().getPlayerList().stream()
                .filter(player -> !player.isDisconnected() && player.getName().toString().contains(event.getFocusedOption().getValue()))
                .map(player -> new Command.Choice(player.getName().toString(), player.getName().toString()))
                .limit(25);

            event.replyChoices(list.toList()).queue();
        }
    }

    private void listCommand(SlashCommandInteractionEvent event) {
        List<ServerPlayerEntity> playerlist = server.getPlayerManager().getPlayerList();

        if (playerlist.isEmpty()) {
            event.reply("There are currently no players online!").setEphemeral(true).queue();
            return;
        }

        String playerList = playerlist.stream()
            .map(player -> "- " + player.getName() + " (" + player.pingMilliseconds + "ms)")
            .collect(Collectors.joining("\n"));

        event.reply(
            "There are " + playerlist.size() + "/" + server.getMaxPlayerCount() + " players online:\n"
                + playerList
        ).setEphemeral(true).queue();
    }

    private void messageCommand(SlashCommandInteractionEvent event) {
        String playerName = Objects.requireNonNull(event.getOption("player")).getAsString();
        String content = Objects.requireNonNull(event.getOption("content")).getAsString();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null) {
            event.reply("The requested player was not found!").setEphemeral(true).queue();
            return;
        }

        if (player.isDisconnected()) {
            event.reply("The requested player is not online!").setEphemeral(true).queue();
            return;
        }

        String name = event.getMember() != null ? event.getMember().getEffectiveName() : event.getUser().getName();

        player.sendMessage(Text.of(name + " : " + content));

        event.reply("The message has been sent to " + player.getName()).setEphemeral(true).queue();
    }
}
