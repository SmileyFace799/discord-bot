package no.smileyface.discordbot.checks;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.checks.CheckFailedException;

/**
 * Checks if the member who fired the event is in a voice channel with the bot.
 * <p>The following checks are also performed implicitly:</p>
 * <ul>
 *     <li>{@link InVoice}</li>
 * </ul>
 */
public class InVoiceWithBot implements Check {
	private final InVoice inVoice;

	public InVoiceWithBot() {
		this.inVoice = new InVoice();
	}

	@Override
	public void check(IReplyCallback event) throws CheckFailedException {
		AudioChannel audioChannel = inVoice.checkAndReturn(event);
		if (!audioChannel.equals(audioChannel.getGuild().getAudioManager().getConnectedChannel())) {
			throw new CheckFailedException(
					"The bot is not connected to the voice channel you're in");
		}
	}
}
