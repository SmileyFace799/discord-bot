package no.smileyface.discordbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * Responsible for storing & organizing tracks.
 */
public class TrackQueue {
    private final Random random;
    private final AudioPlayer player;
    private final List<MusicTrack> queue;
    private final GuildMessageChannel playerChannel;
    private final TrackQueueMessage trackQueueMessage;
    private MusicTrack currentlyPlaying;
    private boolean shuffle;
    private Repeat repeat;

    /**
     * Creates a queue of audio tracks.
     *
     * @param player        The audio player the queue is for.
     * @param playerChannel The message channel where the queue should send status messages in.
     */
    public TrackQueue(AudioPlayer player, GuildMessageChannel playerChannel) {
        this.random = new Random();
        this.player = player;
        this.queue = new LinkedList<>();
        this.playerChannel = playerChannel;
        this.trackQueueMessage = new TrackQueueMessage(this);
        this.currentlyPlaying = null;
        this.shuffle = false;
        this.repeat = Repeat.NO_REPEAT;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public List<MusicTrack> getTracks() {
        return queue;
    }

    public GuildMessageChannel getPlayerChannel() {
        return playerChannel;
    }

    public TrackQueueMessage getTrackQueueMessage() {
        return trackQueueMessage;
    }

    public MusicTrack getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    /**
     * Queues a track, and plays it if the player is not playing anything.
     *
     * @param musicTrack The track to queue
     */
    public synchronized void queue(MusicTrack musicTrack) {
        queue.add(musicTrack);
        if (player.getPlayingTrack() == null) {
            playNext();
        }
    }

    /**
     * If the queue has upcoming tracks. Does not include the currently playing track.
     *
     * @return If the queue has upcoming tracks or not
     */
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    /**
     * Plays the next track.
     */
    public synchronized void playNext() {
        if (repeat == Repeat.REPEAT_SONG) {
            currentlyPlaying = currentlyPlaying.copy();
        } else {
            MusicTrack lastPlayed = currentlyPlaying;
            currentlyPlaying = queue.remove(shuffle ? random.nextInt(queue.size()) : 0);
            if (repeat == Repeat.REPEAT_QUEUE) {
                queue.add(lastPlayed.copy());
            }
        }
        player.playTrack(currentlyPlaying.getAudio());
    }

    /**
     * Skips the currently playing song.
     *
     * @param amount THe amount of tracks to skip.
     */
    public void skip(int amount) {
        for (int i = amount; i > 1; i--) {
            queue.removeFirst();
        }
        player.stopTrack();
    }

    /**
     * Stops the music, and destroys the player.
     */
    public void stop() {
        player.destroy();
        trackQueueMessage.playerClosed();
    }

    public boolean isShuffled() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        trackQueueMessage.updateEmbed();
    }

    public boolean toggleShuffle() {
        setShuffle(!shuffle);
        return shuffle;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
        trackQueueMessage.updateEmbed();
    }

    /**
     * Modes of queue repeats.
     */
    public enum Repeat {
        NO_REPEAT("Off"),
        REPEAT_SONG("Song"),
        REPEAT_QUEUE("Queue");

        private final String str;

        Repeat(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        /**
         * Get a {@link Repeat Repeat} based on it's identifying string.
         *
         * @param str The identifying string to get the {@link Repeat Repeat} for
         * @return The {@link Repeat Repeat} with the provided identifying string
         * @throws IllegalArgumentException If there is no {@link Repeat Repeat}
         *                                  with the provided string identifier
         */
        public static Repeat getRepeat(String str) {
            return Arrays.stream(Repeat.values())
                    .filter(repeat -> repeat.str.equalsIgnoreCase(str))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("No repeat with identifying string \"%s\"", str)
                    ));
        }
    }
}
