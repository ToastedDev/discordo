package dev.toasted.discordo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.toasted.discordo.Discordo;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;

public class ReloadConfigCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
            CommandManager.literal("discordo")
                .then(CommandManager.literal("reload").executes(ReloadConfigCommand::run))
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            Discordo.reloadConfig();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        context.getSource().sendFeedback(() -> Text.literal("Reloaded Discordo!"), true);
        return 1;
    }
}
