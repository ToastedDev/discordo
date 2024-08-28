package dev.toasted.discordo.mixin;

import dev.toasted.discordo.Constants;
import dev.toasted.discordo.Discordo;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info) {
        if(Discordo.INSTANCE == null) return;

        String message = Discordo.INSTANCE.config.messages.join
            .replace("%name%", player.getName().getString());

        if(Discordo.INSTANCE.config.webhookEnabled) {
            Discordo.INSTANCE.webhook
                .sendMessage(message)
                .setUsername(player.getName().getString())
                .setAvatarUrl("https://crafthead.net/avatar/" + player.getUuidAsString())
                .setAllowedMentions(Constants.AllowedMentions)
                .queue();
        } else {
            Discordo.INSTANCE.channel
                .sendMessage(message)
                .setAllowedMentions(Constants.AllowedMentions)
                .queue();
        }
    }
}
