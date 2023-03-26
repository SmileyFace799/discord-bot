package org.smileyface.checks;

import java.util.Objects;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;

/**
 * A bunch of defined checks that check the bot's current state.
 */
public class Checks {
    private Checks() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if the author is in a voice channel.
     *
     * @param member The author that triggered the event.
     * @return The voice channel the member is in
     * @throws CheckFailedException If the member is not in a voice channel
     */
    public static AudioChannel authorInVoice(Member member) throws CheckFailedException {
        GuildVoiceState authorVoiceState = Objects.requireNonNull(
                member.getVoiceState());

        if (!authorVoiceState.inAudioChannel()) {
            throw new CheckFailedException("You're not in a voice channel");
        }
        return Objects.requireNonNull(authorVoiceState.getChannel());
    }

    /**
     * Checks if the bot is not connected to a voice channel in a server.
     *
     * @param audioManager The bot's audio manager in the specified server.
     * @throws CheckFailedException If the bot is connected to a voice channel in the server
     *                              corresponding to the specified audio manager.
     */
    public static void botNotConnected(AudioManager audioManager) throws CheckFailedException {
        if (audioManager.isConnected()) {
            throw new CheckFailedException("The bot is already connected to another voice channel");
        }
    }

    /**
     * Checks if the bot is connected to the same voice channel as the author.
     *
     * @param audioChannel The voice channel the author is connected to
     * @throws CheckFailedException If the bot is not connected to the
     *                              same voice channel as the author
     */
    public static void botConnectedToAuthorVoice(AudioChannel audioChannel)
            throws CheckFailedException {
        if (!audioChannel.equals(audioChannel.getGuild().getAudioManager().getConnectedChannel())) {
            throw new CheckFailedException(
                    "The bot is not connected to the voice channel you're in");
        }
    }

    /**
     * Checks if the bot is playing music.
     *
     * @param guildId The guild to check if the bot is playing music in
     * @return The queue the bot is playing from
     * @throws CheckFailedException If the bot is not playing music
     */
    public static TrackQueue isPlaying(String guildId) throws CheckFailedException {
        TrackQueue queue = MusicManager.getInstance().getQueue(guildId);
        if (queue == null) {
            throw new CheckFailedException("The bot is not playing any music");
        }
        return queue;
    }
}
