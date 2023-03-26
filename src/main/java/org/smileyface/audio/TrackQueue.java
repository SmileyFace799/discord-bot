package org.smileyface.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * Responsible for storing & organizing tracks.
 */
public class TrackQueue {
    private final AudioPlayer player;
    private final List<Track> queue;
    private final GuildMessageChannel playerChannel;
    private Track currentlyPlaying = null;

    /**
     * Creates a queue of audio tracks.
     *
     * @param player The audio player the queue is for.
     * @param playerChannel The message channel where the queue should send status messages in.
     */
    public TrackQueue(AudioPlayer player, GuildMessageChannel playerChannel) {
        this.player = player;
        this.queue = new ArrayList<>();
        this.playerChannel = playerChannel;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public List<Track> getTracks() {
        return queue;
    }

    public GuildMessageChannel getPlayerChannel() {
        return playerChannel;
    }

    public Track getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(Track currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    /**
     * Queues a track, and plays it if the player.
     *
     * @param track The track to queue
     */
    public synchronized void queue(Track track) {
        queue.add(track);
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
    public void playNext() {
        currentlyPlaying = queue.remove(0);
        player.playTrack(currentlyPlaying.getAudio());
    }

    /**
     * Skips the currently playing song.
     */
    public void skip() {
        player.stopTrack();
    }

    /**
     * Stops the music, and destroys the player.
     */
    public void stop() {
        player.destroy();
        playerChannel.sendMessage("> Stopped playing music").queue();
    }
}
