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

    @TomlComment({"Whether or not messages should be sent through a webhook with the player's name and skin"})
    public Boolean webhookEnabled = false;

    @TomlComment({"Configure the messages that are sent to Discord"})
    public Messages messages = new Messages();

    public static class Messages {
        // TODO: add configuration for chat

        @TomlComment({"The message that will be sent when a player receives an advancement"})
        public String advancement = "🏅 %name% has made the advancement **%advancement.name%**\n-# %advancement.description%";

        @TomlComment({"The message that will be sent when a player dies"})
        public String death = "🪦 %deathMessage%";

        @TomlComment({"The message that will be sent when a player joins the server"})
        public String join = "➡️ %name% has joined the game";

        @TomlComment({"The message that will be sent when a player leaves the server"})
        public String leave = "⬅️ %name% has left the game";

        @TomlComment({"The message that will be sent when the server is starting"})
        public String serverStarting = "🔄️ Server starting...";

        @TomlComment({"The message that will be sent when the server has started"})
        public String serverStarted = "✅ Server has started!";

        @TomlComment({"The message that will be sent when the server stops"})
        public String serverStopped = "🛑 Server has stopped.";
    }
}