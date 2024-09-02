package dev.toasted.discordo;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlComment;
import com.moandjiezana.toml.TomlIgnore;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class Config {
    @TomlIgnore
    public static File configFile = new File(
        String.valueOf(
            Path.of(".")
                .resolve("config")
                .resolve(Constants.ModId)
                .resolve("config.toml")
        )
    );

    public static Config loadConfig() throws IOException {
        if(!configFile.exists()) {
            Config config = new Config();
            config.saveConfig();
            return config;
        }
        Config config = new Toml().read(configFile).to(Config.class);
        config.saveConfig();
        return config;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "DuplicatedCode"})
    public void saveConfig() throws IOException {
        if (!configFile.exists()) {
            if (!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }
        final TomlWriter w = new TomlWriter.Builder()
            .indentValuesBy(2)
            .indentTablesBy(4)
            .padArrayDelimitersBy(2)
            .build();
        w.write(this, configFile);
    }

    @TomlComment({"Insert your Discord bot token here", "DO NOT SHARE THIS WITH ANYONE!"})
    public String discordToken = "REPLACE THIS WITH YOUR BOT TOKEN";

    @TomlComment({"The ID of the channel where Minecraft messages will be sent and vice versa"})
    public String channelId = "00000000";


    @TomlComment({"Configure sending messages through webhooks"})
    public Webhook webhook = new Webhook();

    @TomlComment({"Configure the messages that are sent to Discord"})
    public Messages messages = new Messages();

    public static class Webhook {
        @TomlComment({"Whether or not messages should be sent through a webhook"})
        public Boolean enabled = false;

        @TomlComment({"The template to use when setting the name of the webhook"})
        public String name = "%name%";

        @TomlComment({"The template to use when setting the avatar of the webhook"})
        public String avatarUrl = "https://crafthead.net/avatar/%name%";
    }

    public static class Messages {
        // TODO: add configuration for chat

        @TomlComment({"The message that will be sent when a player receives an advancement"})
        public String advancement = ":medal: %name% has made the advancement **%advancement.name%**\n-# %advancement.description%";

        @TomlComment({"The message that will be sent when a player dies"})
        public String death = ":headstone: %deathMessage%";

        @TomlComment({"The message that will be sent when a player joins the server"})
        public String join = ":arrow_right: %name% has joined the game";

        @TomlComment({"The message that will be sent when a player leaves the server"})
        public String leave = ":arrow_left: %name% has left the game";

        @TomlComment({"The message that will be sent when the server is starting"})
        public String serverStarting = ":arrows_counterclockwise: Server starting...";

        @TomlComment({"The message that will be sent when the server has started"})
        public String serverStarted = ":white_check_mark: Server has started!";

        @TomlComment({"The message that will be sent when the server stops"})
        public String serverStopped = ":octagonal_sign: Server has stopped.";
    }
}