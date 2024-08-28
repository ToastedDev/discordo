package dev.toasted.discordo;

import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Discordo implements ModInitializer {
    public static final String MOD_ID = "discordo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Config config = ConfigBuilder.builder(Config::new)
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
        LOGGER.info("Hello world!");
        LOGGER.info("Discord token: " + config.discordToken.get());
    }
}
