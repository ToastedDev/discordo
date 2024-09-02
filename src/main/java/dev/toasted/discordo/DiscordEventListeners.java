package dev.toasted.discordo;

import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiscordEventListeners extends ListenerAdapter {
    private final DiscordToMinecraftLink discordToMcLink;

    public DiscordEventListeners(DiscordToMinecraftLink discordToMcLink) {
        this.discordToMcLink = discordToMcLink;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isWebhookMessage()) return;
        if(event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) return;
        if(!event.getMessage().getChannelId().equals(Discordo.config.channelId)) return;
        if((event.getMessage().getType() != MessageType.DEFAULT) && (event.getMessage().getType() != MessageType.INLINE_REPLY)) return;

        this.discordToMcLink.messageReceivedEvent = event;
        this.discordToMcLink.hasReceivedMessage = true;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getGuild() == null) return;

        switch(event.getName()) {
            case "list": {
                List<ServerPlayerEntity> players = Discordo.getServer().getPlayerManager().getPlayerList();
                StringBuilder message = new StringBuilder("Currently online players ")
                    .append("(")
                    .append(Discordo.getServer().getCurrentPlayerCount())
                    .append("/")
                    .append(Discordo.getServer().getMaxPlayerCount())
                    .append("):\n");

                if(players.isEmpty()) {
                    message = new StringBuilder("No players currently online.");
                } else {
                    for(ServerPlayerEntity player: players) {
                        message.append("- ").append(player.getName().getString()).append("\n");
                    }
                }

                event.reply(message.toString()).setEphemeral(true).queue();
            } break;
            default: break;
        }
    }
}
