package no.smileyface.discordbot.checks;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.checks.CheckFailedException;
import no.smileyface.discordbotframework.checks.InGuild;

/**
 * <p>Checks that the bot is not connected to a voice channel
 * in the same server as the member who fired the event.</p>
 * <p>The following checks are also performed implicitly:</p>
 * <ul>
 *     <li>{@link InGuild}</li>
 * </ul>
 */
public class BotNotInVoice implements Check {
	private final InGuild inGuild;

	public BotNotInVoice() {
		this.inGuild = new InGuild();
	}

	@Override
	public void check(IReplyCallback event) throws CheckFailedException {
		if (inGuild.checkAndReturn(event).getGuild().getAudioManager().isConnected()) {
			throw new CheckFailedException(
					"The bot is already connected to another voice channel");
		}
	}
}
