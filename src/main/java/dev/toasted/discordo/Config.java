package dev.toasted.discordo;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class Config {
    public final ConfigEntry<String> discordToken;
    public final ConfigEntry<String> channelId;
    public final ConfigEntry<Boolean> webhookEnabled;

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
    }
}
