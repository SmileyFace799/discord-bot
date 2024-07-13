package no.smileyface.discordbot;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Set;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
import no.smileyface.discordbot.actions.music.PlayAction;
import no.smileyface.discordbot.actions.music.RemoveAction;
import no.smileyface.discordbot.actions.music.RepeatAction;
import no.smileyface.discordbot.actions.music.ResumePauseAction;
import no.smileyface.discordbot.actions.music.ShowPlayerAction;
import no.smileyface.discordbot.actions.music.ShuffleAction;
import no.smileyface.discordbot.actions.music.SkipAction;
import no.smileyface.discordbot.actions.music.modalcreator.GoToPageModalCreator;
import no.smileyface.discordbot.actions.music.modalcreator.QueueSongModalCreator;
import no.smileyface.discordbot.files.properties.PropertyLoadException;
import no.smileyface.discordbot.files.properties.PropertyNode;
import no.smileyface.discordbot.model.querying.QueryParser;
import no.smileyface.discordbotframework.DiscordBot;

/**
 * Main class for starting the bot.
 */
public class Main {
	/**
	 * Starts the bot.
	 *
	 * @param args java args. This is ignored
	 * @throws NoSuchFileException  If the token file for the active bot is not found
	 * @throws InterruptedException If the bot is interrupted while starting
	 */
	public static void main(
			String[] args
	) throws IOException, InterruptedException, PropertyLoadException {
		try {
			Files.createDirectory(Path.of("botFiles"));

		} catch (FileAlreadyExistsException ignored) {
			// Folder exists, all good
		}
		PropertyNode.loadTree();
		QueryParser queryParser = new QueryParser();
		PropertyNode botNode = PropertyNode.getRoot().getChild("bot");
		String botToken = botNode.getChild(botNode.getChild("active").getValue()).getValue();
		JDA jda = DiscordBot.create(botToken, new InputListener(Set.of(
				// Music
				new GoToPageAction(),
				new JoinAction(),
				new LeaveAction(),
				new PlayAction(queryParser),
				new RemoveAction(),
				new RepeatAction(),
				new ResumePauseAction(),
				new ShowPlayerAction(),
				new ShuffleAction(),
				new SkipAction(),
				// Feedback
				new KnownIssuesAction(),
				new ReportIssueAction(),
				// Misc
				new CreditsAction(),
				new FeaturesAction(queryParser),
				new NotifyAction(),
				new PingAction(),
				new SayAction(),
				// Modal creators
				new GoToPageModalCreator(),
				new QueueSongModalCreator()
		)), GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGES);
		jda.getPresence().setActivity(Activity.playing("/play"));
	}
}
