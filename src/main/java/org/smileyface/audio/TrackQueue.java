package org.smileyface.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.LinkedList;
import java.util.List;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * Responsible for storing & organizing tracks.
 */
public class TrackQueue {
    private final AudioPlayer player;
    private final List<MusicTrack> queue;
    private final GuildMessageChannel playerChannel;
    private final TrackQueueEmbed trackQueueEmbed;
    private MusicTrack currentlyPlaying = null;

    /**
     * Creates a queue of audio tracks.
     *
     * @param player The audio player the queue is for.
     * @param playerChannel The message channel where the queue should send status messages in.
     */
    public TrackQueue(AudioPlayer player, GuildMessageChannel playerChannel) {
        this.player = player;
        this.queue = new LinkedList<>();
        this.playerChannel = playerChannel;
        this.trackQueueEmbed = new TrackQueueEmbed(this);
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

    public TrackQueueEmbed getTrackQueueEmbed() {
        return trackQueueEmbed;
    }

    public MusicTrack getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(MusicTrack currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    /**
     * Queues a track, and plays it if the player.
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
        currentlyPlaying = queue.remove(0);
        player.playTrack(currentlyPlaying.getAudio());
    }

    /**
     * Skips the currently playing song.
     *
     * @param amount THe amount of tracks to skip.
     */
    public void skip(int amount) {
        for (int i = amount; i > 1; i--) {
            queue.remove(0);
        }
        player.stopTrack();
    }

    /**
     * Stops the music, and destroys the player.
     */
    public void stop() {
        player.destroy();
        trackQueueEmbed.playerClosed();
    }
}
