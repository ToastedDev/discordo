package dev.toasted.discordo.mixin;

import dev.toasted.discordo.Constants;
import dev.toasted.discordo.Discordo;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class AdvancementMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;onStatusUpdate(Lnet/minecraft/advancement/AdvancementEntry;)V"))
    public void advancement(AdvancementEntry advancementEntry, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if(!Discordo.isReady()) return;

        final Advancement advancement = advancementEntry.value();
        if(advancement != null && advancement.display().isPresent() && advancement.display().get().shouldAnnounceToChat()) {
            String message = Discordo.config.messages.advancement
                .replace("%name%", owner.getName().getString())
                .replace("%advancement.name%", advancement.display().get().getTitle().getString())
                .replace("%advancement.description%", advancement.display().get().getDescription().getString());
            if(Discordo.config.webhook.enabled) {
                Discordo.webhook
                    .sendMessage(message)
                    .setUsername(
                        Discordo.config.webhook.name
                            .replace("%name%", owner.getName().getString())
                    )
                    .setAvatarUrl(
                        Discordo.config.webhook.avatarUrl
                            .replace("%name%", owner.getName().getString())
                            .replace("%uuid%", owner.getUuidAsString())
                    )
                    .setAllowedMentions(Constants.AllowedMentions)
                    .queue();
            } else {
                Discordo.channel
                    .sendMessage(message)
                    .setAllowedMentions(Constants.AllowedMentions)
                    .queue();
            }
        }
    }
}
