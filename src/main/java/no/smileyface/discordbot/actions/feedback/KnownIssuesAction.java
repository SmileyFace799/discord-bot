package no.smileyface.discordbot.actions.feedback;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.feedback.commands.KnownIssuesCommand;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Shows known issues with the bot.
 */
public class KnownIssuesAction extends BotAction<GenericBotAction.ArgKey> {
    /**
     * Makes the known issues action.
     */
    public KnownIssuesAction(ActionManager manager) {
        super(manager, new KnownIssuesCommand());
    }

    @Override
    protected void execute(
            IReplyCallback event,
            Node<ArgKey, Object> args
    ) {
        event.reply("""
                These are all the known issues with the bot that will not be fixed.
                Please do not report any of these issues, I cannot fix them.

                **Play command with a Spotify link is queuing the wrong song**
                """
                + "  Since Spotify is a paid service, "
                + "it means the bot cannot play music directly from there. "
                + "Instead, when a Spotify link is provided, "
                + "the bot will find the same song on a different platform (such as YouTube), "
                + "and play it from there. However, in very rare cases, "
                + "it might find a different song instead, "
                + "and there isn't much I can do to fix this."
        ).setEphemeral(true).queue();
    }
}
