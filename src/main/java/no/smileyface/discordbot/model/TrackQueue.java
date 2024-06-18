package no.smileyface.discordbot.model;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbot.model.intermediary.QueueEventListener;
import no.smileyface.discordbot.model.intermediary.events.ChangePageEvent;
import no.smileyface.discordbot.model.intermediary.events.MultipleQueuedEvent;
import no.smileyface.discordbot.model.intermediary.events.PauseChangedEvent;
import no.smileyface.discordbot.model.intermediary.events.PlayerStoppedEvent;
import no.smileyface.discordbot.model.intermediary.events.PlaylistQueuedEvent;
import no.smileyface.discordbot.model.intermediary.events.RepeatChangedEvent;
import no.smileyface.discordbot.model.intermediary.events.SetPageEvent;
import no.smileyface.discordbot.model.intermediary.events.ShowPlayerEvent;
import no.smileyface.discordbot.model.intermediary.events.ShuffleChangedEvent;
import no.smileyface.discordbot.model.intermediary.events.TrackQueuedEvent;
import no.smileyface.discordbot.model.intermediary.events.TrackSkippedEvent;
import no.smileyface.discordbot.model.intermediary.events.TrackStartedEvent;

/**
 * Responsible for storing & organizing tracks.
 */
public class TrackQueue {
    private final Random random;
    private final AudioPlayer player;
    private final List<MusicTrack> queue;
    private final GuildMessageChannel playerChannel;

    private QueueEventListener listener;
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
        this.currentlyPlaying = null;
        this.shuffle = false;
        this.repeat = Repeat.NO_REPEAT;

        this.player.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(AudioPlayer player, AudioTrack track) {
                if (!currentlyPlaying.getAudio().equals(track)) {
                    throw new IllegalStateException(
                            "queue.currentlyPlaying isn't the track just started"
                    );
                }
                listener.onMusicEvent(new TrackStartedEvent(currentlyPlaying));
            }

            @Override
            public void onTrackEnd(
                    AudioPlayer player,
                    AudioTrack track,
                    AudioTrackEndReason endReason
            ) {
				if (hasNext()) {
					playNext();
				} else {
					if (!MusicManager.getInstance().stop(getPlayerChannel().getGuild())) {
                        stop(null);
                    }
				}
            }
        });
    }

    /**
     * Sets the music listener to listen for changes in the queue.
     *
     * @param listener The music listener to set
     */
    public void setListener(QueueEventListener listener) {
        this.listener = listener;
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

    public MusicTrack getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public boolean isShuffled() {
        return shuffle;
    }

    public Repeat getRepeat() {
        return repeat;
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
     * Queues multiple tracks & playlists.
     *
     * @param audioItems The tracks & playlists to queue
     * @param queuedBy   THe member who queued the tracks & playlists
     */
    public synchronized void queue(
            List<AudioItem> audioItems,
            Member queuedBy
    ) {
        audioItems
                .stream()
                .flatMap(item -> switch (item) {
                    case AudioTrack track -> Stream.of(track);
                    case AudioPlaylist playlist -> playlist.getTracks().stream();
                    default -> null;
                })
                .filter(Objects::nonNull)
                .map(audio -> new MusicTrack(audio, queuedBy))
                .forEach(queue::add);
        if (player.getPlayingTrack() == null) {
            playNext();
        }
        listener.onMusicEvent(new MultipleQueuedEvent(queuedBy));
    }

    /**
     * Queues a track, and plays it if the player is not playing anything.
     *
     * @param audioTrack The audio to queue
     * @param queuedBy   The member who queued it
     */
    public synchronized void queue(
            AudioTrack audioTrack,
            Member queuedBy
    ) {
        MusicTrack queuedTrack = new MusicTrack(audioTrack, queuedBy);
        queue.add(queuedTrack);
        if (player.getPlayingTrack() == null) {
            playNext();
        }
        listener.onMusicEvent(new TrackQueuedEvent(queuedTrack));
    }

    /**
     * Queues a single playlist.
     *
     * @param audioPlaylist The playlist queued
     * @param queuedBy      The member who queued the playlist
     */
    public synchronized void queue(AudioPlaylist audioPlaylist, Member queuedBy) {
        for (AudioTrack audio : audioPlaylist.getTracks()) {
            queue.add(new MusicTrack(audio, queuedBy));
        }
        if (player.getPlayingTrack() == null) {
            playNext();
        }
        listener.onMusicEvent(new PlaylistQueuedEvent(audioPlaylist, queuedBy));
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
     * Sets the paused state of the player.
     *
     * @param paused   The new pause state
     * @param pausedBy The member who updated the pause state
     */
    public void setPaused(boolean paused, Member pausedBy) {
        player.setPaused(paused);
        listener.onMusicEvent(new PauseChangedEvent(paused, pausedBy));
    }

    /**
     * Sets the shuffle state of the player.
     *
     * @param shuffled   The new shuffle state
     * @param shuffledBy The member who updated the shuffled state
     */
    public void setShuffled(boolean shuffled, Member shuffledBy) {
        this.shuffle = shuffled;
        listener.onMusicEvent(new ShuffleChangedEvent(shuffle, shuffledBy));
    }

    public void setRepeat(Repeat repeat, Member changedBy) {
        this.repeat = repeat;
        listener.onMusicEvent(new RepeatChangedEvent(repeat, changedBy));
    }

    /**
     * Skips one or more tracks in the queue.
     *
     * @param skippedBy The member who skipped one or more tracks
     * @param amount THe amount of tracks to skip.
     * @return The amount of tracks actually skipped (Different If provided amount is out of bounds)
     */
    public int skip(Member skippedBy, int amount) {
        amount = Math.clamp(amount, 1, queue.size() + 1);
        for (int i = amount; i > 1; i--) {
            queue.removeFirst();
        }
        player.stopTrack();
        listener.onMusicEvent(new TrackSkippedEvent(skippedBy, amount));
        return amount;
    }

    /**
     * Stops the music, and destroys the player.
     */
    public void stop(Member stoppedBy) {
        player.destroy();
		listener.onMusicEvent(new PlayerStoppedEvent(stoppedBy));
    }

    public void setPage(int page, IntConsumer callback) {
        listener.onMusicEvent(new SetPageEvent(page, callback));
    }

    public void changePage(boolean increment, BiConsumer<Integer, Boolean> callback) {
        listener.onMusicEvent(new ChangePageEvent(increment, callback));
    }
    
    public void showPlayerMessage() {
        listener.onMusicEvent(new ShowPlayerEvent());
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
            return Arrays
                    .stream(Repeat.values())
                    .filter(repeat -> repeat.str.equalsIgnoreCase(str))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("No repeat with identifying string \"%s\"", str)
                    ));
        }
    }
}
