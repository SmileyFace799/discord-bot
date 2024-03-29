package org.smileyface.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.smileyface.commands.music.LeaveCommand;

/**
 * Schedules tracks for audio players.
 */
public class TrackEventListener extends AudioEventAdapter {
    private static TrackEventListener instance;

    /**
     * Singleton.
     *
     * @return Singleton instance.
     */
    public static synchronized TrackEventListener getInstance() {
        if (instance == null) {
            instance = new TrackEventListener();
        }
        return instance;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        MusicManager.getInstance()
                .getQueue(player)
                .getTrackQueueMessage()
                .togglePaused();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        MusicManager.getInstance()
                .getQueue(player)
                .getTrackQueueMessage()
                .togglePaused();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        TrackQueue queue = MusicManager.getInstance().getQueue(player);
        MusicTrack startedMusicTrack = queue.getCurrentlyPlaying();
        if (!startedMusicTrack.getAudio().equals(track)) {
            throw new IllegalStateException("queue.currentlyPlaying isn't the track just started");
        }
        queue.getTrackQueueMessage().updateEmbed();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        TrackQueue queue = MusicManager.getInstance().getQueue(player);
        if (endReason.mayStartNext && queue.hasNext()) {
            queue.playNext();
        } else {
            if (queue.hasNext()) {
                queue.playNext();
            } else {
                LeaveCommand.leaveSilently(queue.getPlayerChannel().getGuild());
            }
        }
    }
}
