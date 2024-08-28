package dev.toasted.discordo;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Discordo implements ModInitializer {
    public static final String MOD_ID = "discordo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Discordo INSTANCE;
    public final Config config = ConfigBuilder.builder(Config::new)
        .path(
            Path.of(".")
                .resolve("config")
                .resolve(MOD_ID)
                .resolve("config.properties")
        )
        .keepOrder(true)
        .removeUnused(true)
        .strict(true)
        .saveAfterBuild(true)
        .build();
    public static TextChannel channel;
    public static Webhook webhook;

    @Override
    public void onInitialize() {
        Discordo.INSTANCE = this;
        if(Objects.equals(config.discordToken.get(), "")) {
            LOGGER.error("No Discord token specified. Please specify your bot's Discord token in config/discordo/config.properties.");
        } else {
            try {
                initializeDiscord();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final DiscordToMinecraftLink discordToMcLink = new DiscordToMinecraftLink();
    private Message serverStartMessage;

    public void initializeDiscord() throws InterruptedException {
        // TODO: add slash commands
        JDA jda = JDABuilder.createLight(
            config.discordToken.get(),
            EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
        )
            .addEventListeners(new MessageReceiveListener(discordToMcLink))
            .build();

        jda.awaitReady();

        LOGGER.info("Logged into Discord as {}", jda.getSelfUser().getAsTag());

        channel = jda.getTextChannelById(config.channelId.get());
        if(channel == null) {
            LOGGER.error("Channel could not be found.");
            return;
        }

        if(config.webhookEnabled.get()) {
            webhook = getWebhook(channel);
            if(webhook == null) {
                webhook = channel.createWebhook("Minecraft Chat Link").complete();
            }
        }

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, parameters) -> {
            if(config.webhookEnabled.get()) {
                webhook
                    .sendMessage(message.getContent().getString())
                    .setUsername(sender.getName().getString())
                    .setAvatarUrl("https://crafthead.net/avatar/" + sender.getUuidAsString())
                    .setAllowedMentions(Constants.AllowedMentions)
                    .queue();
            } else {
                channel
                    .sendMessage(sender.getName().getString() + ": " + message.getContent().getString())
                    .setAllowedMentions(Constants.AllowedMentions)
                    .queue();
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if(entity instanceof PlayerEntity) {
                if(config.webhookEnabled.get()) {
                    webhook
                        .sendMessage("💀 " + source.getDeathMessage(entity).getString())
                        .setUsername(entity.getName().getString())
                        .setAvatarUrl("https://crafthead.net/avatar/" + entity.getUuidAsString())
                        .setAllowedMentions(Constants.AllowedMentions)
                        .queue();
                } else {
                    channel
                        .sendMessage("💀 " + source.getDeathMessage(entity).getString())
                        .setAllowedMentions(Constants.AllowedMentions)
                        .queue();
                }
            }
        });

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            serverStartMessage =
                channel
                    .sendMessage(config.serverStartingMessage.get())
                    .setAllowedMentions(Constants.AllowedMentions)
                    .complete();
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            serverStartMessage
                .editMessage(config.serverStartedMessage.get())
                .setAllowedMentions(Constants.AllowedMentions)
                .queue();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
           channel
               .sendMessage(config.serverStoppedMessage.get())
               .setAllowedMentions(Constants.AllowedMentions)
               .queue();
           jda.shutdown();
        });

        ServerTickEvents.START_SERVER_TICK.register(discordToMcLink::serverTick);
    }

    private Webhook getWebhook(TextChannel channel) {
        List<Webhook> webhooks = channel.retrieveWebhooks().complete();
        for(Webhook webhook: webhooks) {
            if(webhook.getName().equals("Minecraft Chat Link")) {
                return webhook;
            }
        }
        return null;
    }
}
