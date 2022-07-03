package me.seren.discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static me.seren.KingsWorld.config;
import static me.seren.KingsWorld.logger;

public class Listener extends ListenerAdapter {
  public Client client;

  public Listener(Client client) {
    this.client = client;
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    logger.info(event.getJDA().getSelfUser().getAsTag() + " is now online");
    this.client.addAndUpdateCommands(event.getJDA());
  }

  @Override
  public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    switch (event.getName()) {
      case "list":
        String[] names = this.client.server.getPlayerNames();
        int current = this.client.server.getCurrentPlayerCount();
        int max = this.client.server.getMaxPlayerCount();

        StringBuilder builder = new StringBuilder()
          .append(String.format("There are %o of %o players online", current, max))
          .append(System.lineSeparator()).append(System.lineSeparator());

        if (names.length != 0) {
          for (String name : names) builder.append("• ").append(name).append("\n");
        }

        event.reply(builder.toString()).setEphemeral(true).queue();
        break;

      case "message":
        break;
    }
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    String content = event.getMessage().getContentRaw();
    User author = event.getAuthor();
    String channelId = event.getMessage().getChannel().getId();
    if (event.isWebhookMessage() || author.isBot() || author.isSystem()) return;
    if (content.isBlank() || !channelId.equals(config.getChannelId())) return;

    this.client.server.getPlayerManager().broadcast(Text.literal(
      String.format("<%s> %s", author.getAsTag(), content)
    ), MessageType.SYSTEM);
  }
}
