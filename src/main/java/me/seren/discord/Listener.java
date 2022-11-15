package me.seren.discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.seren.KingsWorld.logger;
import static me.seren.KingsWorld.modConfig;

public class Listener extends ListenerAdapter {
  public Client client;

  public Listener(Client client) {
    this.client = client;
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    logger.info(event.getJDA().getSelfUser().getAsTag() + " is now online");
    event.getJDA().updateCommands().addCommands(
      Commands.slash("list", "View a list of online players"),
      Commands.slash("message", "Message a player that is online")
        .addOption(OptionType.STRING, "player", "The player to message", true, true)
        .addOption(OptionType.STRING, "content", "The message you want to send", true)
    ).queue();
  }

  @Override
  public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    switch (event.getName()) {
      case "list" -> {
        String[] names = this.client.server.getPlayerNames();
        int current = this.client.server.getCurrentPlayerCount();
        int max = this.client.server.getMaxPlayerCount();
        StringBuilder builder = new StringBuilder()
          .append(String.format("There are %o of %o players online", current, max))
          .append("\n\n");
        if (names.length != 0) {
          for (String name : names) builder.append("• ").append(name).append("\n");
        }
        event.reply(builder.toString()).setEphemeral(true).queue();
      }
      case "message" -> {
        String playerName = event.getOptions().get(0).getAsString();
        String content = event.getOptions().get(1).getAsString();
        ServerPlayerEntity player = this.client.server.getPlayerManager().getPlayer(playerName);

        if (player == null || player.isDisconnected()) {
          event.reply("Provided player was not found or is not online")
            .setEphemeral(true).queue();
          return;
        }

        player.sendChatMessage(
          SignedMessage.of(Text.of(content)),
          MessageSender.of(Text.of(event.getUser().getAsTag())),
          MessageType.MSG_COMMAND
        );
        event.reply("Your message has been sent to " + player.getEntityName())
          .setEphemeral(true).queue();
      }
    }
  }

  @Override
  public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
    if (event.getName().equals("message") && event.getFocusedOption().getName().equals("player")) {
      List<Command.Choice> options = this.client.server.getPlayerManager().getPlayerList().stream()
        .filter(p -> !p.isDisconnected())
        .map(p -> new Command.Choice(p.getEntityName(), p.getEntityName()))
        .toList();
      event.replyChoices(options).queue();
    }
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    String content = event.getMessage().getContentRaw();
    User author = event.getAuthor();
    String channelId = event.getMessage().getChannel().getId();
    if (event.isWebhookMessage() || author.isBot() || author.isSystem()) return;
    if (content.isBlank() || !channelId.equals(modConfig.getChannelId())) return;

    this.client.server.getPlayerManager().broadcast(Text.literal(
      String.format("<%s> %s", author.getAsTag(), content)
    ), MessageType.SYSTEM);
  }
}
