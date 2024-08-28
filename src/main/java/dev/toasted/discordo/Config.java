package dev.toasted.discordo;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class Config {
    public final ConfigEntry<String> discordToken;
    public final ConfigEntry<String> channelId;
    public final ConfigEntry<Boolean> webhookEnabled;
    public final ConfigEntry<String> serverStartingMessage;
    public final ConfigEntry<String> serverStartedMessage;
    public final ConfigEntry<String> serverStoppedMessage;

    public Config(ConfigBuilder builder) {
        builder.header(
            "Discordo config version 1.0.0"
        );
        discordToken = builder
            .stringEntry("discord_token", "")
            .comment("The Discord bot token that the mod will use to connect to Discord and send messages");
        channelId = builder
            .stringEntry("channel_id", "")
            .comment("The ID of the channel where Minecraft messages will be sent and vice versa");
        webhookEnabled = builder
            .booleanEntry("webhook_enabled", false)
            .comment("Whether or not messages should be sent through a webhook with the player's name and skin");
        serverStartingMessage = builder
            .stringEntry("server_starting_message", "Server starting...")
            .comment("The message that will be sent when the server is starting");
        serverStartedMessage = builder
            .stringEntry("server_started_message", "Server started!")
            .comment("The message that will be sent when the server has started");
        serverStoppedMessage = builder
            .stringEntry("server_stopped_message", "Server has stopped.")
            .comment("The message that will be sent when the server stops");
    }
}
