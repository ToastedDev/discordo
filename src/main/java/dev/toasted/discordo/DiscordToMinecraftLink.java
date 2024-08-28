package dev.toasted.discordo;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DiscordToMinecraftLink {
    public MessageReceivedEvent messageReceivedEvent;
    public Boolean hasReceivedMessage = false;

    public void serverTick(MinecraftServer server) {
        if(this.hasReceivedMessage) {
            MutableText message = Text.literal("[Discord] ").formatted(Formatting.BLUE)
                    .append(
                        Text.literal(this.messageReceivedEvent.getMember().getEffectiveName())
                            .setStyle(Style.EMPTY.withColor(this.messageReceivedEvent.getMember().getColorRaw()))
                    )
                    .append(
                        Text.literal(": ").formatted(Formatting.WHITE)
                    );
            if(this.messageReceivedEvent.getMessage().getContentRaw().isEmpty() &&
                    !this.messageReceivedEvent.getMessage().getAttachments().isEmpty()) {
                message
                    .append(
                        Text.literal("sent an attachment")
                            .formatted(Formatting.WHITE, Formatting.ITALIC)
                    );
            } else {
                message
                    .append(
                        Text.literal(this.messageReceivedEvent.getMessage().getContentRaw())
                            .formatted(Formatting.WHITE)
                    );
            }
            for(ServerPlayerEntity player: server.getPlayerManager().getPlayerList()) {
                player.sendMessage(message, false);
            }
            this.hasReceivedMessage = false;
        }
    }
}
