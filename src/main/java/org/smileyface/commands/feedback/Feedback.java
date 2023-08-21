package org.smileyface.commands.feedback;

import java.util.List;
import org.smileyface.commands.Category;

/**
 * Command category for feedback commands.
 */
public class Feedback extends Category {
    private static Feedback instance;

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized Feedback getInstance() {
        if (instance == null) {
            instance = new Feedback();
        }
        return instance;
    }

    private Feedback() {
        super(List.of(
                new KnownIssuesCommand(),
                new ReportIssueCommand()
        ));
    }
}
