package dev.toasted.discordo;

import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceiveListener extends ListenerAdapter {
    private final DiscordBot discordBot;

    public MessageReceiveListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isWebhookMessage()) return;
        // TODO: only check if the author is the logged in bot
        if(event.getAuthor().isBot()) return;
        if((event.getMessage().getType() != MessageType.DEFAULT) && (event.getMessage().getType() != MessageType.INLINE_REPLY)) return;

        Discordo.LOGGER.info("Recieved message: " + event.getMessage().getContentRaw());

        this.discordBot.messageReceivedEvent = event;
        this.discordBot.hasReceivedMessage = true;
    }
}
