package org.smileyface.commands.categories;

import java.util.List;
import org.smileyface.commands.Category;
import org.smileyface.commands.music.JoinCommand;
import org.smileyface.commands.music.LeaveCommand;
import org.smileyface.commands.music.PauseCommand;
import org.smileyface.commands.music.PlayCommand;
import org.smileyface.commands.music.QueueCommand;
import org.smileyface.commands.music.RepeatCommand;
import org.smileyface.commands.music.ResumeCommand;
import org.smileyface.commands.music.ShowPlayerCommand;
import org.smileyface.commands.music.ShuffleCommand;
import org.smileyface.commands.music.SkipCommand;

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
                new ShowPlayerCommand(),
                new RepeatCommand(),
                new ShuffleCommand()
        ));
    }
}
