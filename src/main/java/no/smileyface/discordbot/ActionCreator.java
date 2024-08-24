package no.smileyface.discordbot;

import java.util.Collection;
import java.util.Set;
import no.smileyface.discordbot.actions.feedback.KnownIssuesAction;
import no.smileyface.discordbot.actions.feedback.ReportIssueAction;
import no.smileyface.discordbot.actions.misc.CreditsAction;
import no.smileyface.discordbot.actions.misc.FeaturesAction;
import no.smileyface.discordbot.actions.misc.NotifyAction;
import no.smileyface.discordbot.actions.misc.PingAction;
import no.smileyface.discordbot.actions.misc.SayAction;
import no.smileyface.discordbot.actions.music.GoToPageAction;
import no.smileyface.discordbot.actions.music.JoinAction;
import no.smileyface.discordbot.actions.music.LeaveAction;
import no.smileyface.discordbot.actions.music.LyricsAction;
import no.smileyface.discordbot.actions.music.PlayAction;
import no.smileyface.discordbot.actions.music.RemoveAction;
import no.smileyface.discordbot.actions.music.RepeatAction;
import no.smileyface.discordbot.actions.music.ResumePauseAction;
import no.smileyface.discordbot.actions.music.ShowPlayerAction;
import no.smileyface.discordbot.actions.music.ShuffleAction;
import no.smileyface.discordbot.actions.music.SkipAction;
import no.smileyface.discordbot.model.querying.QueryParser;
import no.smileyface.discordbotframework.ActionInitializer;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import org.jetbrains.annotations.NotNull;

/**
 * Creates all bot actions.
 */
public class ActionCreator implements ActionInitializer {
	@NotNull
	@Override
	public Collection<? extends BotAction<? extends GenericBotAction.ArgKey>> createActions(
			ActionManager manager
	) {
		QueryParser queryParser = new QueryParser();
		GoToPageAction goToPageAction = new GoToPageAction(manager);
		PlayAction playAction = new PlayAction(manager, queryParser);
		return Set.of(
				// Music
				goToPageAction,
				new JoinAction(manager),
				new LeaveAction(manager),
				new LyricsAction(manager),
				new PlayAction(manager, queryParser),
				new RemoveAction(manager),
				new RepeatAction(manager),
				new ResumePauseAction(manager),
				new ShowPlayerAction(manager),
				new ShuffleAction(manager),
				new SkipAction(manager),
				// Feedback
				new KnownIssuesAction(manager),
				new ReportIssueAction(manager),
				// Misc
				new CreditsAction(manager),
				new FeaturesAction(manager, queryParser),
				new NotifyAction(manager),
				new PingAction(manager),
				new SayAction(manager),
				// Modal creators
				goToPageAction.getModalCreator(),
				playAction.getModalCreator()
		);
	}
}
