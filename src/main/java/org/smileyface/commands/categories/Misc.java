package org.smileyface.commands.categories;

import java.util.List;
import org.smileyface.commands.Category;
import org.smileyface.commands.misc.CreditsCommand;
import org.smileyface.commands.misc.FeaturesCommand;
import org.smileyface.commands.misc.PingCommand;
import org.smileyface.commands.misc.SayCommand;

/**
 * Contains all miscellaneous commands that don't fit anywhere else.
 */
public class Misc extends Category {
    private static Misc instance;

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized Misc getInstance() {
        if (instance == null) {
            instance = new Misc();
        }
        return instance;
    }

    private Misc() {
        super(List.of(
                new PingCommand(),
                new SayCommand(),
                new FeaturesCommand(),
                new CreditsCommand()
        ));
    }
}
