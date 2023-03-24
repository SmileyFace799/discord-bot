package org.smileyface.commands;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllCommands {
    private AllCommands() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<String, BotCommand> ALL_COMMANDS = Stream
            .of(
                    Misc.getCommands()
            )
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static Map<String, BotCommand> get() {
        return ALL_COMMANDS;
    }
}
