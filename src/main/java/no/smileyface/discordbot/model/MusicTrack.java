package no.smileyface.discordbot.model;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;

/**
 * Stores info about a queued track.
 */
public class MusicTrack {
    private final AudioTrack audio;
    private final AudioTrackInfo info;
    private final Member queuedBy;

    /**
     * Creates a new playable track.
     *
     * @param audio The track audio
     * @param queuedBy The member who queued the track
     */
    public MusicTrack(AudioTrack audio, Member queuedBy) {
        this.audio = audio;
        this.info = audio.getInfo();
        this.queuedBy = queuedBy;
    }

    public AudioTrack getAudio() {
        return audio;
    }

    public String getTitle() {
        return info.title;
    }

    public String getAuthor() {
        return info.author;
    }

    public String getLink() {
        return info.uri;
    }

    public Member getQueuedBy() {
        return queuedBy;
    }

    public MusicTrack copy() {
        return new MusicTrack(audio.makeClone(), queuedBy);
    }
}
