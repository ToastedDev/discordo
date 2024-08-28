package dev.toasted.discordo.mixin;

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
    ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;onStatusUpdate(Lnet/minecraft/advancement/AdvancementEntry;)V"))
    public void advancement(AdvancementEntry advancementEntry, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if(Discordo.INSTANCE == null) return;
        final Advancement advancement = advancementEntry.value();
        if(advancement != null && advancement.display().isPresent() && advancement.display().get().shouldAnnounceToChat()) {
            String message = "🏅 " + owner.getName().getString() + " has made the advancement **" +
                    advancement.display().get().getTitle().getString() + "**\n*" +
                    advancement.display().get().getDescription().getString() + "*";
            if(Discordo.INSTANCE.config.webhookEnabled.get()) {
                Discordo.INSTANCE.webhook
                    .sendMessage(message)
                    .setUsername(owner.getName().getString())
                    .setAvatarUrl("https://crafthead.net/avatar/" + owner.getUuidAsString())
                    .queue();
            } else {
                Discordo.INSTANCE.channel
                    .sendMessage(message)
                    .queue();
            }
        }
    }
}
