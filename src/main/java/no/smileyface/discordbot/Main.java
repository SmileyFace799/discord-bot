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
import no.smileyface.discordbot.commands.feedback.KnownIssuesAction;
import no.smileyface.discordbot.commands.feedback.ReportIssueAction;
import no.smileyface.discordbot.commands.misc.CreditsAction;
import no.smileyface.discordbot.commands.misc.FeaturesAction;
import no.smileyface.discordbot.commands.misc.NotifyAction;
import no.smileyface.discordbot.commands.misc.PingAction;
import no.smileyface.discordbot.commands.misc.SayAction;
import no.smileyface.discordbot.commands.music.GoToPageAction;
import no.smileyface.discordbot.commands.music.JoinAction;
import no.smileyface.discordbot.commands.music.LeaveAction;
import no.smileyface.discordbot.commands.music.PlayAction;
import no.smileyface.discordbot.commands.music.RemoveAction;
import no.smileyface.discordbot.commands.music.RepeatAction;
import no.smileyface.discordbot.commands.music.ResumePauseAction;
import no.smileyface.discordbot.commands.music.ShowPlayerAction;
import no.smileyface.discordbot.commands.music.ShuffleAction;
import no.smileyface.discordbot.commands.music.SkipAction;
import no.smileyface.discordbot.commands.music.modalcreator.GoToPageModalCreator;
import no.smileyface.discordbot.commands.music.modalcreator.QueueSongModalCreator;
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
	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			Files.createDirectory(Path.of("botFiles"));

		} catch (FileAlreadyExistsException ignored) {
			// Folder exists, all good
		}
		JDA jda = DiscordBot.create(new InputListener(Set.of(
				// Music
				new GoToPageAction(),
				new JoinAction(),
				new LeaveAction(),
				new PlayAction(),
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
				new FeaturesAction(),
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
