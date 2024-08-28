package dev.toasted.discordo;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceiveListener extends ListenerAdapter {
    private final DiscordToMinecraftLink discordToMcLink;
    private final JDA jda;

    public MessageReceiveListener(DiscordToMinecraftLink discordToMcLink, JDA jda) {
        this.discordToMcLink = discordToMcLink;
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isWebhookMessage()) return;
        if(this.jda.getSelfUser().getId().equals(event.getAuthor().getId())) return;
        if((event.getMessage().getType() != MessageType.DEFAULT) && (event.getMessage().getType() != MessageType.INLINE_REPLY)) return;

        Discordo.LOGGER.info("Recieved message: " + event.getMessage().getContentRaw());

        this.discordToMcLink.messageReceivedEvent = event;
        this.discordToMcLink.hasReceivedMessage = true;
    }
}
