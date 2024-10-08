package no.smileyface.discordbot.checks;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.checks.CheckFailedException;
import no.smileyface.discordbotframework.checks.InGuild;

/**
 * Checks if the bot is playing music in the same guild as the member who fired the event..
 * <p>The following checks are also performed:</p>
 * <ul>
 *     <li>{@link InGuild}</li>
 * </ul>
 *
 */
public class BotIsPlaying implements Check {
	private final InGuild inGuild;

	public BotIsPlaying() {
		this.inGuild = new InGuild();
	}

	@Override
	public void check(IReplyCallback event) throws CheckFailedException {
		if (MusicManager.getInstance()
				.getQueue(inGuild.checkAndReturn(event)
						.getGuild()
						.getIdLong()
				) == null
		) {
			throw new CheckFailedException("The bot is not playing any music");
		}
	}
}
