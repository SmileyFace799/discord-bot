package no.smileyface.discordbot.checks;

import java.util.Objects;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.checks.CheckAndReturn;
import no.smileyface.discordbotframework.checks.CheckFailedException;
import no.smileyface.discordbotframework.checks.InGuild;

/**
 * Checks if the member who fired the event is in a voice channel.
 * <p>The following checks are also performed implicitly:</p>
 * <ul>
 *     <li>{@link InGuild}</li>
 * </ul>
 *
 */
public class InVoice implements CheckAndReturn<AudioChannel> {
	private final InGuild inGuild;

	public InVoice() {
		this.inGuild = new InGuild();
	}

	@Override
	public AudioChannel checkAndReturn(IReplyCallback event) throws CheckFailedException {
		GuildVoiceState authorVoiceState = Objects.requireNonNull(
				inGuild.checkAndReturn(event).getVoiceState());

		if (!authorVoiceState.inAudioChannel()) {
			throw new CheckFailedException("You're not in a voice channel");
		}
		return Objects.requireNonNull(authorVoiceState.getChannel());
	}
}
