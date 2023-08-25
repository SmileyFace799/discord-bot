package org.smileyface.commands.categories;

import java.util.List;
import org.smileyface.commands.Category;
import org.smileyface.commands.feedback.KnownIssuesCommand;
import org.smileyface.commands.feedback.ReportIssueCommand;

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
