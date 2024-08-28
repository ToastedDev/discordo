package dev.toasted.discordo;

import net.dv8tion.jda.api.entities.Message;

import java.util.EnumSet;

public class Constants {
    public static EnumSet<Message.MentionType> AllowedMentions = EnumSet.of(
        Message.MentionType.CHANNEL,
        Message.MentionType.EMOJI,
        Message.MentionType.SLASH_COMMAND,
        Message.MentionType.USER
    );
}
