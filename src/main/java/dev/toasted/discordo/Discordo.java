package dev.toasted.discordo;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Discordo implements ModInitializer {
    public static final String MOD_ID = "discordo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
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
    private TextChannel channel = null;
    private Webhook webhook = null;

    @Override
    public void onInitialize() {
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
                    .queue();
            } else {
                channel
                    .sendMessage(sender.getName().getString() + ": " + message.getContent().getString())
                    .queue();
            }
        });


        ServerTickEvents.START_SERVER_TICK.register(discordToMcLink::serverTick);
    }

    public Webhook getWebhook(TextChannel channel) {
        List<Webhook> webhooks = channel.retrieveWebhooks().complete();
        for(Webhook webhook: webhooks) {
            if(webhook.getName().equals("Minecraft Chat Link")) {
                return webhook;
            }
        }
        return null;
    }
}
