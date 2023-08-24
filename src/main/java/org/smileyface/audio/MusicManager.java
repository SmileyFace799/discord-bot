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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

/**
 * Responsible for creating audio players & queuing songs to them.
 */
public class MusicManager {
    private static MusicManager instance;
    private final AudioPlayerManager playerManager;
    private final Map<Long, TrackQueue> queues;

    private MusicManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        queues = new HashMap<>();
    }

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
        queues.put(playerChannel.getGuild().getIdLong(), new TrackQueue(player, playerChannel));
        return player;
    }

    public TrackQueue getQueue(long guildId) {
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
     * @param queuedBy   The member who queued the audio.
     * @param hook       The {@link InteractionHook} to used when responding to the command
     *                   that queued the audio.
     */
    public void queue(String identifier, Member queuedBy, InteractionHook hook) {
        TrackQueue queue = queues.get(queuedBy.getGuild().getIdLong());
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audio) {
                queue.queue(new MusicTrack(audio, queuedBy));
                String title = audio.getInfo().title;
                hook.sendMessage("Song/video added to queue: " + title).queue();
                queue.getTrackQueueMessage()
                        .setLastCommand(queuedBy, "Queued \"" + title + "\"");
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    for (AudioTrack audio : playlist.getTracks()) {
                        queue.queue(new MusicTrack(audio, queuedBy));
                    }
                    String title = playlist.getName();
                    hook.sendMessage("Playlist added to queue: " + title).queue();
                    queue.getTrackQueueMessage()
                            .setLastCommand(queuedBy, "Queued \"" + title + "\"");
                }
            }

            @Override
            public void noMatches() {
                hook.sendMessage("Couldn't find song/video/playlist").queue();
            }

            @Override
            public void loadFailed(FriendlyException fe) {
                if (fe.severity.equals(FriendlyException.Severity.COMMON)) {
                    hook.sendMessage(fe.getMessage()).queue();
                } else {
                    hook.sendMessage("An unknown error occurred").queue();
                    Logger.getLogger(getClass().getName()).log(
                            Level.WARNING, "Audio load failed: {0}", fe.getMessage()
                    );
                }
            }
        });
    }

    /**
     * Loads multiple tracks / playlists.
     *
     * @param identifiers The unique identifiers for each track / playlist
     * @param queuedBy    The member who queued the audios
     * @param hook        The {@link InteractionHook} to used when responding to the command
     *                    that queued the audios
     */
    public void queueMultiple(List<String> identifiers, Member queuedBy, InteractionHook hook) {
        TrackQueue queue = queues.get(queuedBy.getGuild().getIdLong());
        for (String identifier : identifiers) {
            playerManager.loadItemOrdered(0, identifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audio) {
                    queue.queue(new MusicTrack(audio, queuedBy));
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    if (playlist.isSearchResult()) {
                        trackLoaded(playlist.getTracks().get(0));
                    } else {
                        for (AudioTrack audio : playlist.getTracks()) {
                            queue.queue(new MusicTrack(audio, queuedBy));
                        }
                    }
                }

                @Override
                public void noMatches() {
                    //Do nothing
                }

                @Override
                public void loadFailed(FriendlyException fe) {
                    if (!fe.severity.equals(FriendlyException.Severity.COMMON)) {
                        Logger.getLogger(getClass().getName()).log(
                                Level.WARNING, "Audio load failed: {0}", fe.getMessage()
                        );
                    }
                }
            });
        }
        hook.sendMessage("Your songs / videos / playlists are being queued!\n"
                + "(Note: Any not found will be skipped)").queue();
        queue.getTrackQueueMessage()
                .setLastCommand(queuedBy, "Queued multiple songs / videos / playlists");
    }

    public void stop(long guildId) {
        queues.remove(guildId).stop();
    }
}
