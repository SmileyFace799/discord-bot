package org.smileyface.commands.feedback;

import java.util.List;
import org.smileyface.commands.Category;

public class Feedback extends Category {
    private static Feedback instance;

    public static synchronized Feedback getInstance() {
        if (instance == null) {
            instance = new Feedback();
        }
        return instance;
    }

    public Feedback() {
        super(List.of(
                new KnownIssuesCommand(),
                new ReportIssueCommand()
        ));
    }
}
