package dev.toasted.discordo;

import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceiveListener extends ListenerAdapter {
    private final DiscordToMinecraftLink discordToMcLink;

    public MessageReceiveListener(DiscordToMinecraftLink discordToMcLink) {
        this.discordToMcLink = discordToMcLink;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isWebhookMessage()) return;
        if(event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) return;
        if((event.getMessage().getType() != MessageType.DEFAULT) && (event.getMessage().getType() != MessageType.INLINE_REPLY)) return;

        Discordo.LOGGER.info("Recieved message: " + event.getMessage().getContentRaw());

        this.discordToMcLink.messageReceivedEvent = event;
        this.discordToMcLink.hasReceivedMessage = true;
    }
}
