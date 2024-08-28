package dev.toasted.discordo;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
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

    @Override
    public void onInitialize() {
        if(Objects.equals(config.discordToken.get(), "")) {
            LOGGER.error("No Discord token specified. Please specify your bot's Discord token in config/discordo/config.properties.");
        } else {
            initializeDiscord();
        }
    }

    public void initializeDiscord() {
        // TODO: add slash commands
        JDA jda = JDABuilder.createLight(config.discordToken.get())
            .build();

        LOGGER.info("Logged into Discord as " + jda.getSelfUser().getAsTag());

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, parameters) -> {
            TextChannel channel = jda.getTextChannelById(config.channelId.get());
            if(channel == null) {
                LOGGER.error("Channel could not be found.");
                return;
            }
            channel
                .sendMessage(sender.getName().getString() + ": " + message.getContent().getString())
                .queue();
        });
    }
}
