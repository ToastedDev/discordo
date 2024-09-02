package dev.toasted.discordo;

import dev.toasted.discordo.command.ReloadConfigCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class Discordo implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.ModId);
    private static Boolean ready = false;
    public static Config config;
    private static JDA jda;

    static {
        try {
            config = Config.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TextChannel channel;
    public static Webhook webhook;

    @Override
    public void onInitialize() {
        if(config.discordToken.isEmpty() || config.discordToken.equals("REPLACE THIS WITH YOUR BOT TOKEN")) {
            LOGGER.error("No Discord token specified. Please specify your bot's Discord token in config/discordo/config.toml.");
        } else {
            try {
                registerMcCommands();
                initializeDiscord();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void reloadConfig() throws InterruptedException, IOException {
        Discordo.config = Config.loadConfig();

        if(config.discordToken.isEmpty() || config.discordToken.equals("REPLACE THIS WITH YOUR BOT TOKEN")) {
            LOGGER.error("No Discord token specified. Please specify your bot's Discord token in config/discordo/config.toml.");
        } else {
            jda.shutdown();
            jda = null;
            Discordo.initJda();
        }
    }

    private void registerMcCommands() {
        CommandRegistrationCallback.EVENT.register(ReloadConfigCommand::register);
    }

    public static final DiscordToMinecraftLink discordToMcLink = new DiscordToMinecraftLink();
    private Message serverStartMessage;
    private static MinecraftServer server;

    private static void initJda() throws InterruptedException {
        jda = JDABuilder.createLight(
                        config.discordToken,
                        EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                )
                .addEventListeners(new DiscordEventListeners(discordToMcLink))
                .build();

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
            Commands.slash("list", "List all players currently online on the server")
                .setGuildOnly(true)
        ).queue();

        commands.queue();

        jda.awaitReady();

        channel = jda.getTextChannelById(config.channelId);
        if(channel == null) {
            LOGGER.error("Channel could not be found.");
            return;
        }

        if(config.webhook.enabled) {
            webhook = getWebhook(channel);
            if(webhook == null) {
                webhook = channel.createWebhook("Minecraft Chat Link").complete();
            }
        }
    }

    public void initializeDiscord() throws InterruptedException {
        initJda();

        LOGGER.info("Logged into Discord as {}", jda.getSelfUser().getAsTag());
        ready = true;

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, parameters) -> {
            if(config.webhook.enabled) {
                webhook
                    .sendMessage(message.getContent().getString())
                    .setUsername(
                        config.webhook.name
                            .replace("%name%", sender.getName().getString())
                    )
                    .setAvatarUrl(
                        config.webhook.avatarUrl
                            .replace("%name%", sender.getName().getString())
                            .replace("%uuid%", sender.getUuidAsString())
                    )
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
            if(!(entity instanceof PlayerEntity)) return;

            String message = config.messages.death.replace("%deathMessage%", source.getDeathMessage(entity).getString());

            if(config.webhook.enabled) {
                webhook
                    .sendMessage(message)
                    .setUsername(
                        config.webhook.name
                            .replace("%name%", entity.getName().getString())
                    )
                    .setAvatarUrl(
                        config.webhook.avatarUrl
                            .replace("%name%", entity.getName().getString())
                            .replace("%uuid%", entity.getUuidAsString())
                    )
                    .setAllowedMentions(Constants.AllowedMentions)
                    .queue();
            } else {
                channel
                    .sendMessage(message)
                    .setAllowedMentions(Constants.AllowedMentions)
                    .queue();
            }
        });

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            Discordo.server = server;
            serverStartMessage =
                channel
                    .sendMessage(config.messages.serverStarting)
                    .setAllowedMentions(Constants.AllowedMentions)
                    .complete();
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            serverStartMessage
                .editMessage(config.messages.serverStarted)
                .setAllowedMentions(Constants.AllowedMentions)
                .queue();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
           channel
               .sendMessage(config.messages.serverStopped)
               .setAllowedMentions(Constants.AllowedMentions)
               .queue();
           jda.shutdown();
        });

        ServerTickEvents.START_SERVER_TICK.register(discordToMcLink::serverTick);
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static Boolean isReady() {
        return ready;
    }

    private static Webhook getWebhook(TextChannel channel) {
        List<Webhook> webhooks = channel.retrieveWebhooks().complete();
        for(Webhook webhook: webhooks) {
            if(webhook.getName().equals("Minecraft Chat Link")) {
                return webhook;
            }
        }
        return null;
    }
}
