package org.smileyface.commands;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.smileyface.commands.categories.Feedback;
import org.smileyface.commands.categories.Misc;
import org.smileyface.commands.categories.Music;
import org.smileyface.generics.GenericManager;

/**
 * A middle manager between the bot itself & its commands.
 */
public class CommandManager extends GenericManager<BotCommand> {
    private static CommandManager instance;

    private CommandManager() {
        super(Stream
                .of(
                        Misc.getInstance().getItems(),
                        Music.getInstance().getItems(),
                        Feedback.getInstance().getItems()
                )
                .map(Map::entrySet)
                .flatMap(Set::stream)
        );
    }

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }
}
