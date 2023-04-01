package org.smileyface.commands.misc;

import java.util.List;
import org.smileyface.commands.Category;

/**
 * Contains all miscellaneous commands that don't fit anywhere else.
 */
public class Misc extends Category {
    private static Misc instance;

    public static synchronized Misc getInstance() {
        if (instance == null) {
            instance = new Misc();
        }
        return instance;
    }

    public Misc() {
        super(List.of(
                new PingCommand(),
                new SayCommand()
        ));
    }
}
