package org.smileyface.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

/**
 * Responsible for creating audio players & queuing songs to them.
 */
public class MusicManager {
    private static MusicManager instance;

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    private final AudioPlayerManager playerManager;
    private final Map<String, TrackQueue> queues;

    private MusicManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        queues = new HashMap<>();
    }

    /**
     * Creates an audio player for the specified guild.
     *
     * @param playerChannel The channel where the player should be created.
     *                      This is not the audio channel the bot is playing in,
     *                      but the text channel where
     * @return The audio player created
     */
    public AudioPlayer createPlayer(GuildMessageChannel playerChannel) {
        AudioPlayer player = playerManager.createPlayer();
        player.addListener(TrackEventListener.getInstance());
        queues.put(playerChannel.getGuild().getId(), new TrackQueue(player, playerChannel));
        playerChannel.sendMessage("> Joined a voice channel, ready to play music").queue();
        return player;
    }

    public TrackQueue getQueue(String guildId) {
        return queues.get(guildId);
    }

    /**
     * Gets the queue that belongs to an audio player.
     *
     * @param audioPlayer The audio player to find the queue for
     * @return The queue that belongs to the specified audio player
     */
    public TrackQueue getQueue(AudioPlayer audioPlayer) {
        Optional<TrackQueue> optionalQueue = queues
                .values()
                .stream()
                .filter(trackQueue -> trackQueue.getPlayer().equals(audioPlayer))
                .findFirst();
        return optionalQueue.orElse(null);
    }

    /**
     * Loads a track / playlist.
     *
     * @param identifier The unique identifier for the track / playlist
     * @param queuedBy The member who queued the audio.
     */
    public void queue(String identifier, Member queuedBy, InteractionHook hook)
            throws FriendlyException {
        TrackQueue queue = queues.get(queuedBy.getGuild().getId());
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audio) {
                queue.queue(new Track(audio, queuedBy));
                hook.sendMessage("Song/video added to queue: " + audio.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack audio : playlist.getTracks()) {
                    queue.queue(new Track(audio, queuedBy));
                }
                hook.sendMessage("Playlist added to queue: " + playlist.getName()).queue();
            }

            @Override
            public void noMatches() {
                hook.sendMessage("Couldn't find song/video/playlist").setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException fe) {
                if (fe.severity.equals(FriendlyException.Severity.COMMON)) {
                    hook.sendMessage(fe.getMessage()).queue();
                } else {
                    hook.sendMessage("An unknown error occurred").setEphemeral(true).queue();
                    System.out.println("loadFailed: " + fe.getMessage());
                }
            }
        });
    }

    public void stop(String guildId) {
        queues.remove(guildId).stop();
    }
}
