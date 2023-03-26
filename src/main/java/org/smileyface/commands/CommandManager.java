package org.smileyface.commands;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.smileyface.checks.CheckFailedException;

/**
 * A middle manager between the bot itself & its commands.
 */
public class CommandManager {
    private CommandManager() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * A map of all the commands the bot has.
     */
    private static final Map<String, BotCommand> ALL_COMMANDS = Stream
            .of(
                    Misc.getCommands(),
                    Music.getCommands()
            )
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    static {
        for (BotCommand command : ALL_COMMANDS.values().stream().toList()) {
            Collection<String> nicknames = command.getNicknames();
            for (String nickname : nicknames) {
                SlashCommandData commandData = command.getData();
                BotCommand shortcutCommand = new BotCommand(Commands
                        .slash(nickname, "Shortcut for /" + commandData.getName())
                        .addOptions(commandData.getOptions())
                        .setGuildOnly(commandData.isGuildOnly())
                        .setDefaultPermissions(commandData.getDefaultPermissions())
                        .setNSFW(commandData.isNSFW())
                ) {
                    @Override
                    public void run(SlashCommandInteractionEvent event)
                            throws CheckFailedException {
                        command.run(event);
                    }
                };
                ALL_COMMANDS.put(shortcutCommand.getData().getName(), shortcutCommand);
            }
        }
    }

    public static Collection<BotCommand> getCommands() {
        return ALL_COMMANDS.values();
    }

    /**
     * gets a specific command.
     *
     * @param commandName The name of the command.
     * @return The command associated with the name.
     * @throws IllegalArgumentException If there is no command with the specified name.
     */
    public static BotCommand getCommand(String commandName) {
        if (!ALL_COMMANDS.containsKey(commandName)) {
            throw new IllegalArgumentException("No command with name \"" + commandName + "\"");
        }
        return ALL_COMMANDS.get(commandName);
    }
}
