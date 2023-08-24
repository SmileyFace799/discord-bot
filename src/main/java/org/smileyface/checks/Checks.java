package org.smileyface.checks;

import java.util.Objects;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.smileyface.audio.MusicManager;

/**
 * A bunch of defined checks to check the current state of the bot.
 */
public class Checks {
    private Checks() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if an interaction was invoked in a guild.
     *
     * @param event The invocation event
     * @throws ChecksFailedException If the interaction was not invoked in a guild
     */
    public static void authorInGuild(IReplyCallback event) throws ChecksFailedException {
        getAuthorInGuild(event);
    }

    private static Member getAuthorInGuild(IReplyCallback event) throws ChecksFailedException {
        if (!event.isFromGuild()) {
            throw new ChecksFailedException("You're not in a server");
        }
        return Objects.requireNonNull(event.getMember());
    }

    /**
     * Checks if the author of an interaction is in a voice channels.
     * <p>The following checks are also performed:</p>
     * <ul>
     *     <li>{@link #authorInGuild(IReplyCallback)}</li>
     * </ul>
     *
     * @param event The invocation event
     * @throws ChecksFailedException If the author is not in a voice channel
     */
    public static void authorInVoice(IReplyCallback event) throws ChecksFailedException {
        getAuthorInVoice(event);
    }

    private static AudioChannel getAuthorInVoice(IReplyCallback event)
            throws ChecksFailedException {
        GuildVoiceState authorVoiceState = Objects.requireNonNull(
                getAuthorInGuild(event).getVoiceState());

        if (!authorVoiceState.inAudioChannel()) {
            throw new ChecksFailedException("You're not in a voice channel");
        }
        return Objects.requireNonNull(authorVoiceState.getChannel());
    }

    /**
     * Checks if the bot is not connected to a voice channel in a server.
     * <p>The following checks are also performed:</p>
     * <ul>
     *     <li>{@link #authorInGuild(IReplyCallback)}</li>
     * </ul>
     *
     * @param event The invocation event
     * @throws ChecksFailedException If the bot is connected to a voice channel in the server
     *                                corresponding to the specified audio manager.
     */
    public static void botNotConnected(IReplyCallback event) throws ChecksFailedException {
        if (getAuthorInGuild(event).getGuild().getAudioManager().isConnected()) {
            throw new ChecksFailedException(
                    "The bot is already connected to another voice channel");
        }
    }

    /**
     * Checks if the bot is connected to the same voice channel as the author.
     * <p>The following checks are also performed:</p>
     * <ul>
     *     <li>
     *         {@link #authorInVoice(IReplyCallback)}
     *         <ul>
     *             <li>{@link #authorInGuild(IReplyCallback)}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param event The invocation event
     * @throws ChecksFailedException If the bot is not connected to the
     *                                same voice channel as the member
     */
    public static void botConnectedToAuthorVoice(IReplyCallback event)
            throws ChecksFailedException {
        AudioChannel audioChannel = getAuthorInVoice(event);
        if (!audioChannel.equals(audioChannel.getGuild().getAudioManager().getConnectedChannel())) {
            throw new ChecksFailedException(
                    "The bot is not connected to the voice channel you're in");
        }
    }

    /**
     * Checks if the bot is playing music.
     * <p>The following checks are also performed:</p>
     * <ul>
     *     <li>{@link #authorInGuild(IReplyCallback)}</li>
     * </ul>
     *
     * @param event The invocation event
     * @throws ChecksFailedException If the bot is not playing music
     */
    public static void isPlaying(IReplyCallback event) throws ChecksFailedException {
        if (MusicManager.getInstance()
                .getQueue(getAuthorInGuild(event)
                        .getGuild()
                        .getIdLong()
                ) == null
        ) {
            throw new ChecksFailedException("The bot is not playing any music");
        }
    }
}
