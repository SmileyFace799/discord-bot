package org.smileyface.commands;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smileyface.commands.feedback.Feedback;
import org.smileyface.commands.misc.Misc;
import org.smileyface.commands.music.Music;

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
                    Misc.getInstance().getCommands(),
                    Music.getInstance().getCommands(),
                    Feedback.getInstance().getCommands()
            )
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
