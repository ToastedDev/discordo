package dev.toasted.discordo.mixin;

import dev.toasted.discordo.Constants;
import dev.toasted.discordo.Discordo;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class NetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "HEAD"), method = "onDisconnected")
    private void onPlayerLeave(DisconnectionInfo info, CallbackInfo ci) {
        if(Discordo.INSTANCE == null) return;

        String message = Discordo.INSTANCE.config.messages.leave
            .replace("%name", player.getName().getString());

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
