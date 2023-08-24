package org.smileyface.commands.music;

import java.util.List;
import org.smileyface.commands.Category;

/**
 * Contains all music-related commands.
 */
public class Music extends Category {
    private static Music instance;

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    private Music() {
        super(List.of(
                new JoinCommand(),
                new LeaveCommand(),
                new PlayCommand(),
                new SkipCommand(),
                new ResumeCommand(),
                new PauseCommand(),
                new QueueCommand(),
                new ShowPlayerCommand()
        ));
    }
}
