package dev.toasted.discordo;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DiscordBot {
    public MessageReceivedEvent messageReceivedEvent;
    public Boolean hasReceivedMessage = false;

    public void serverTick(MinecraftServer server) {
        if(this.hasReceivedMessage) {
            Text message = Text.literal("[Discord] ").formatted(Formatting.DARK_BLUE)
                .append(
                    Text.literal(this.messageReceivedEvent.getMember().getEffectiveName())
                        .setStyle(Style.EMPTY.withColor(this.messageReceivedEvent.getMember().getColorRaw()))
                )
                .append(
                    Text.literal(": " + this.messageReceivedEvent.getMessage().getContentRaw())
                        .formatted(Formatting.WHITE)
                );
            for(ServerPlayerEntity player: server.getPlayerManager().getPlayerList()) {
                player.sendMessage(message, false);
            }
            this.hasReceivedMessage = false;
        }
    }
}
