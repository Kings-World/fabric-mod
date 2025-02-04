package net.kings_world.fabric_mod.discord;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Formatters {
    public static String formatMessage(Message message) {
        String formattedMessage = message.getContentRaw();
        formattedMessage = formatCustomEmojis(formattedMessage);
        formattedMessage = formatMentions(formattedMessage, message);
        formattedMessage = formatMarkdown(formattedMessage);

        return Stream.of(formattedMessage, formatAttachments(message), formatStickers(message))
            .filter(s -> !s.isBlank())
            .collect(Collectors.joining(" "));
    }

    public static String formatAuthor(Message message) {
        if (message.isWebhookMessage()) {
            return Objects.requireNonNull(message.getInteractionMetadata()).getUser().getEffectiveName();
        }

        Member member = message.getMember();
        if (member != null) return member.getEffectiveName();

        return message.getAuthor().getEffectiveName();
    }

    public static String formatReference(Message message) {
        Message reference = message.getReferencedMessage();
        if (reference == null) return "Unknown Reference";
        return formatAuthor(reference);
    }

    public static String formatAttachments(Message message) {
        if (message.getAttachments().isEmpty()) return "";
        return message.getAttachments().stream()
            .map(attachment -> "attachment://" + stringNoSpaces(attachment.getFileName()))
            .collect(Collectors.joining(" "));
    }

    public static String formatStickers(Message message) {
        if (message.getStickers().isEmpty()) return "";
        return message.getStickers().stream()
            .map(sticker -> "sticker://" + stringNoSpaces(sticker.getName()))
            .collect(Collectors.joining(" "));
    }

    public static String formatCustomEmojis(String content) {
        return content.replaceAll("<a?:([^:]+):(\\d{17,20})>", ":$1:");
    }

    public static String formatMentions(String content, Message message) {
        String formattedContent = content;
        Mentions mentions = message.getMentions();

        for (Member member : mentions.getMembers()) {
            formattedContent = formattedContent.replaceAll("<@!?" + member.getId() + ">", "@" + member.getEffectiveName());
        }

        for (User user : mentions.getUsers()) {
            formattedContent = formattedContent.replaceAll("<@!?" + user.getId() + ">", "@" + user.getEffectiveName());
        }

        for (Role role : mentions.getRoles()) {
            formattedContent = formattedContent.replaceAll("<@&" + role.getId() + ">", "@" + role.getName());
        }

        for (GuildChannel channel : mentions.getChannels()) {
            formattedContent = formattedContent.replaceAll("<#" + channel.getId() + ">", "#" + channel.getName());
        }

        return formattedContent;
    }

    public static String formatMarkdown(String content) {
        // credits to https://github.com/cindyaddoil/Regex-Tuesday-Challenge#challenge-4
        return content
            .replaceAll("(^|[^*])\\*{3}([^*]+.?[^*])\\*{3}(?=[^*]|$)", "$1§l§o$2§r§7") // bold and italic
            .replaceAll("(^|[^*])\\*{2}([^*]+.?[^*])\\*{2}(?=[^*]|$)", "$1§l$2§r§7") // bold
            .replaceAll("(^|[^*])\\*([^*]+.?[^*])\\*(?=[^*]|$)", "$1§o$2§r§7") // italic
            .replaceAll("(^|[^_])_{2}([^_].+?[^_])_{2}(?=[^_]|$)", "$1§n$2§r§7") // underline
            .replaceAll("(^|[^~])~{2}([^~].+?[^~])~{2}(?=[^~]|$)", "$1§m$2§r§7"); // strikethrough
    }

    private static String stringNoSpaces(String string) {
        return string.replaceAll("\\s+", "_");
    }
}
