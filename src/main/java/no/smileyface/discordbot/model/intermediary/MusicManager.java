package no.smileyface.discordbot.model.intermediary;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import no.smileyface.discordbot.Properties;
import no.smileyface.discordbot.actions.music.PlayAction;
import no.smileyface.discordbot.model.MusicTrack;
import no.smileyface.discordbot.model.TrackQueue;
import no.smileyface.discordbot.model.intermediary.events.BotJoinedEvent;
import no.smileyface.discordbot.view.TrackQueueMessage;
import no.smileyface.discordbotframework.Identifier;

/**
 * Responsible for creating audio players & queuing songs to them.
 */
public class MusicManager {
	private static MusicManager instance;
	private final AudioPlayerManager playerManager;
	private final Map<Long, TrackQueue> queues;

	private MusicManager() {
		playerManager = new DefaultAudioPlayerManager();
		YoutubeAudioSourceManager source = new YoutubeAudioSourceManager();
		String ytOauthToken = Properties
				.getChild("token")
				.getChild("youtube")
				.getChild("oauth")
				.getValue();
		// No valid token should satisfy this condition,
		// so this works as a simple "safeguard" against gibberish input
		if (ytOauthToken != null && (ytOauthToken.length() < 30 || ytOauthToken.contains(" "))) {
			ytOauthToken = null;
		}
		source.useOauth2(ytOauthToken, ytOauthToken != null);
		playerManager.registerSourceManager(source);
		AudioSourceManagers.registerRemoteSources(playerManager,
				com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class
		);
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
	 * @param audioChannel  The audio channel the bot should play in
	 * @param playerChannel The channel where the player should be created.
	 *                      This is not the audio channel the bot is playing in,
	 *                      but the text channel where
	 * @param identifier    An identifier the player may use to find user inputs
	 * @param createdBy     The member who created the audio player
	 * @see #createPlayerIfNotExists(AudioChannel, GuildMessageChannel, Identifier, Member)
	 */
	public void createPlayer(
			AudioChannel audioChannel,
			GuildMessageChannel playerChannel,
			Identifier identifier,
			Member createdBy
	) {
		AudioManager audioManager = audioChannel.getGuild().getAudioManager();
		if (audioManager.isConnected()) {
			throw new IllegalStateException("The bot is already connected to a voice channel");
		}

		AudioPlayer player = playerManager.createPlayer();
		audioManager.setSendingHandler(new LavaPlayerJdaWrapper(player));
		audioManager.openAudioConnection(audioChannel);

		TrackQueue queue = new TrackQueue(player, playerChannel);
		TrackQueueMessage message = new TrackQueueMessage(queue, identifier);
		queue.setListener(message);
		if (createdBy != null) {
			message.onMusicEvent(new BotJoinedEvent(createdBy));
		}
		queues.put(playerChannel.getGuild().getIdLong(), queue);
	}

	/**
	 * Creates an audio player for the specified guild, if it doesn't already exist.
	 *
	 * @param audioChannel  The audio channel the bot should play in
	 * @param playerChannel The channel where the player should be created.
	 *                      This is not the audio channel the bot is playing in,
	 *                      but the text channel where
	 * @param identifier    An identifier the player may use to find user inputs
	 * @param createdBy     The member who created the audio player
	 * @see #createPlayer(AudioChannel, GuildMessageChannel, Identifier, Member)
	 */
	public void createPlayerIfNotExists(
			AudioChannel audioChannel,
			GuildMessageChannel playerChannel,
			Identifier identifier,
			Member createdBy
	) {
		try {
			createPlayer(audioChannel, playerChannel, identifier, createdBy);
		} catch (IllegalStateException ise) {
			// Do nothing
		}
	}

	public TrackQueue getQueue(long guildId) {
		return queues.get(guildId);
	}

	/**
	 * Gets the currently playing music track.
	 *
	 * @param guild The guild to get the current track in
	 * @return The currently playing track, if there is one. {@code null} otherwise
	 */
	public MusicTrack getCurrentTrack(Guild guild) {
		MusicTrack currentTrack = null;
		if (guild != null) {
			TrackQueue queue = queues.get(guild.getIdLong());
			if (queue != null) {
				currentTrack = queue.getCurrentlyPlaying();
			}
		}
		return currentTrack;
	}

	/**
	 * Loads a track / playlist.
	 *
	 * @param identifier The unique identifier for the track / playlist
	 * @param queuedBy   The member who queued the audio.
	 * @param hook       The {@link InteractionHook} to used when responding to the command
	 *                   that queued the audio.
	 */
	public void queue(
			String identifier,
			PlayAction.UndoContextAction undoAction,
			Member queuedBy,
			InteractionHook hook
	) {
		playerManager.loadItem(identifier, new Queuer(queuedBy, undoAction, hook));
	}

	/**
	 * Loads multiple tracks / playlists.
	 *
	 * @param identifiers         The unique identifiers for each track / playlist
	 * @param queuedBy            The member who queued the audios
	 * @param hook                The {@link InteractionHook} to used when responding to the command
	 *                            that queued the audios
	 * @param amountFailedToParse The amount of queries that failed parsing
	 */
	public void queueMultiple(
			List<String> identifiers,
			PlayAction.UndoContextAction undoAction,
			Member queuedBy,
			InteractionHook hook,
			int amountFailedToParse
	) {
		CompletableFuture.runAsync(() -> {
			TrackQueue queue = queues.get(queuedBy.getGuild().getIdLong());
			Queuer queuer =
					new Queuer(queuedBy, undoAction, hook, identifiers.size(), amountFailedToParse);
			identifiers.forEach(identifier -> playerManager.loadItemOrdered(
					queue, identifier, queuer
			));
		});
	}

	public void remove(List<MusicTrack> toRemove, Guild guild) {
		queues.get(guild.getIdLong()).remove(toRemove);
	}

	/**
	 * Removes songs from {@code startIndex} to {@code endIndex}.
	 * All indexes in this method are treated as user input,
	 * meaning {@code 1} is the 1st element of the queue, and {@code queue.size()} is the last.
	 * All indexes are also inclusive.
	 *
	 * @param startIndex The start index to remove songs from. Will be clamped to fit the queue size
	 * @param endIndex   The end index to remove songs to. Will be clamped to fit the queue size
	 * @param removedBy  The member that removed the songs
	 * @param postHook   A post-operation hook, with the clamped start & end values
	 */
	public void remove(
			int startIndex,
			int endIndex,
			Member removedBy,
			BiConsumer<Integer, Integer> postHook
	) {
		queues.get(removedBy.getGuild().getIdLong()).remove(
				startIndex,
				endIndex,
				removedBy,
				postHook
		);
	}

	/**
	 * Sets the paused state of the player.
	 *
	 * @param paused   The new pause state
	 * @param pausedBy The member who updated the pause state
	 */
	public void setPaused(boolean paused, Member pausedBy) {
		queues.get(pausedBy.getGuild().getIdLong()).setPaused(paused, pausedBy);
	}

	/**
	 * Toggles the pause state of the player.
	 *
	 * @param pausedBy The member who toggled the pause state
	 * @return The player's new pause state
	 */
	public boolean togglePaused(Member pausedBy) {
		TrackQueue queue = queues.get(pausedBy.getGuild().getIdLong());
		boolean paused = !queue.isPaused();
		queue.setPaused(paused, pausedBy);
		return paused;
	}

	/**
	 * Toggles the shuffle state of the player.
	 *
	 * @param shuffledBy The member who toggled the shuffled state
	 * @return The player's new shuffle state
	 */
	public boolean toggleShuffled(Member shuffledBy) {
		TrackQueue queue = queues.get(shuffledBy.getGuild().getIdLong());
		boolean shuffled = !queue.isShuffled();
		queue.setShuffled(shuffled, shuffledBy);
		return shuffled;
	}

	/**
	 * Sets the repeat mode of the player.
	 *
	 * @param repeat    The new repeat mode
	 * @param changedBy The member who changed the repeat mode
	 */
	public void setRepeat(TrackQueue.Repeat repeat, Member changedBy) {
		queues.get(changedBy.getGuild().getIdLong()).setRepeat(repeat, changedBy);
	}

	/**
	 * Changes the repeat mode of the player to the next mode.
	 * The mode order is as follows:
	 * <br/>{@link TrackQueue.Repeat#NO_REPEAT NO_REPEAT}
	 * -> {@link TrackQueue.Repeat#REPEAT_SONG REPEAT_SONG}
	 * -> {@link TrackQueue.Repeat#REPEAT_QUEUE REPEAT_QUEUE}
	 *
	 * @param changedBy The member who changed the repeat mode
	 */
	public TrackQueue.Repeat changeRepeat(Member changedBy) {
		TrackQueue queue = queues.get(changedBy.getGuild().getIdLong());
		TrackQueue.Repeat repeat = switch (queue.getRepeat()) {
			case NO_REPEAT -> TrackQueue.Repeat.REPEAT_SONG;
			case REPEAT_SONG -> TrackQueue.Repeat.REPEAT_QUEUE;
			case REPEAT_QUEUE -> TrackQueue.Repeat.NO_REPEAT;
		};
		queue.setRepeat(repeat, changedBy);
		return repeat;
	}

	public int skip(Member skippedBy, int amount) {
		return queues.get(skippedBy.getGuild().getIdLong()).skip(skippedBy, amount);
	}

	/**
	 * Stops the music in a guild, and destroys the player, if it exists.
	 *
	 * @param guild     The guild to stop music in
	 * @param stoppedBy The member who stopped the music, if any.
	 *                  Can be {@code null} if the player stopped from natural causes
	 * @return If the player existed & was subsequently destroyed
	 */
	private boolean stop(Guild guild, Member stoppedBy) {
		guild.getAudioManager().closeAudioConnection();
		TrackQueue queue = queues.remove(guild.getIdLong());
		if (queue != null) {
			queue.stop(stoppedBy);
		}
		return queue == null;
	}

	/**
	 * Stops the music in a guild, and destroys the player, if it exists.
	 *
	 * @param stoppedBy The member who stopped the music
	 * @return If the player existed & was subsequently destroyed
	 * @see #stop(Guild)
	 */
	public boolean stop(Member stoppedBy) {
		return stop(stoppedBy.getGuild(), stoppedBy);
	}

	/**
	 * Stops the music in a guild, and destroys the player, if it exists.
	 *
	 * @param guild The guild to stop music in
	 * @return If the player existed & was subsequently destroyed
	 * @see #stop(Member)
	 */
	public boolean stop(Guild guild) {
		return stop(guild, null);
	}

	public void setPage(Guild guild, int page, IntConsumer callback) {
		queues.get(guild.getIdLong()).setPage(page, callback);
	}

	public void decrementPage(Guild guild, BiConsumer<Integer, Boolean> callback) {
		queues.get(guild.getIdLong()).changePage(false, callback);
	}

	public void incrementPage(Guild guild, BiConsumer<Integer, Boolean> callback) {
		queues.get(guild.getIdLong()).changePage(true, callback);
	}

	public void showPlayerMessage(Guild guild) {
		queues.get(guild.getIdLong()).showPlayerMessage();
	}

	private class Queuer implements AudioLoadResultHandler {
		private final TrackQueue queue;
		private final Member queuedBy;
		private final PlayAction.UndoContextAction undoAction;
		private final InteractionHook hook;
		private final List<AudioItem> loadedAudios;
		private final int numberOfQueues;
		private final int amountFailedToParse;

		private int foundCounter;
		private int notFoundCounter;
		private int failedCounter;
		private long lastMessageUpdate;
		private String baseResponse;

		private Queuer(
				Member queuedBy,
				PlayAction.UndoContextAction undoAction,
				InteractionHook hook,
				int numberOfQueues,
				int amountFailedToParse
		) {
			this.queue = queues.get(queuedBy.getGuild().getIdLong());
			this.queuedBy = queuedBy;
			this.undoAction = undoAction;
			this.hook = hook;
			this.loadedAudios = new ArrayList<>();
			this.numberOfQueues = numberOfQueues;
			this.amountFailedToParse = amountFailedToParse;

			this.foundCounter = 0;
			this.notFoundCounter = 0;
			this.failedCounter = 0;
			this.lastMessageUpdate = Long.MIN_VALUE;
			this.baseResponse = "Your songs / videos / playlists are being queued! "
					+ "(This may take a while)\nThe queue will update once all songs are queued";

			if (numberOfQueues > 1) {
				hook.sendMessage(baseResponse).queue();
				this.lastMessageUpdate = System.nanoTime();
			}
		}

		public Queuer(
				Member queuedBy,
				PlayAction.UndoContextAction undoAction,
				InteractionHook hook
		) {
			this(queuedBy, undoAction, hook, 1, 0);
		}

		private void checkAllTracksLoaded() {
			boolean done = false;
			if ((foundCounter + notFoundCounter + failedCounter)
					>= numberOfQueues && !loadedAudios.isEmpty()
			) {
				queue.queue(loadedAudios, queuedBy, undoAction::addTracksToUndo);
				this.baseResponse = "All songs / playlists / videos queued!";
				done = true;
			}

			long timeNow = System.nanoTime();
			if (!hook.isExpired()
					&& (done || timeNow > (lastMessageUpdate + 3e9))
			) {
				StringBuilder response = new StringBuilder(baseResponse);
				if (foundCounter > 0) {
					response.append("\n- Successfully queued: ").append(foundCounter);
				}
				if (notFoundCounter > 0) {
					response.append("\n- Not found: ").append(notFoundCounter);
				}
				if (amountFailedToParse > 0) {
					response.append("\n- Failed to parse: ").append(amountFailedToParse);
				}
				if (failedCounter > 0) {
					response.append("\n- Failed to queue: ").append(failedCounter);
				}
				WebhookMessageEditAction<Message> editAction =
						hook.editOriginal(response.toString());
				if (done) {
					editAction.setActionRow(undoAction.getButtons());
				}
				editAction.queue();
				lastMessageUpdate = timeNow;
			}
		}

		private void loadAnotherAudio(AudioItem item) {
			loadedAudios.add(item);
			foundCounter++;
			checkAllTracksLoaded();
		}

		@Override
		public void trackLoaded(AudioTrack audio) {
			if (numberOfQueues == 1) {
				queue.queue(audio, queuedBy, undoAction::addTracksToUndo);
				if (hook != null && !hook.isExpired()) {
					hook.sendMessage("Song/video added to queue: " + audio.getInfo().title)
							.addActionRow(undoAction.getButtons())
							.queue();
				}
			} else {
				loadAnotherAudio(audio);
			}
		}

		@Override
		public void playlistLoaded(AudioPlaylist playlist) {
			if (playlist.isSearchResult()) {
				trackLoaded(playlist.getTracks().getFirst());
			} else if (numberOfQueues == 1) {
				queue.queue(playlist, queuedBy, undoAction::addTracksToUndo);
				if (hook != null && !hook.isExpired()) {
					hook.sendMessage("Playlist added to queue: " + playlist.getName()).queue();
				}
			} else {
				loadAnotherAudio(playlist);
			}
		}

		@Override
		public void noMatches() {
			if (numberOfQueues > 1) {
				notFoundCounter++;
				checkAllTracksLoaded();
			} else if (hook != null && !hook.isExpired()) {
				hook.sendMessage("Couldn't find song/video/playlist").queue();
			}
		}

		@Override
		public void loadFailed(FriendlyException fe) {
			if (numberOfQueues > 1) {
				failedCounter++;
				checkAllTracksLoaded();
			} else if (!fe.severity.equals(FriendlyException.Severity.COMMON)) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING,
						"Audio load failed", fe
				);
				if (hook != null && !hook.isExpired()) {
					hook.sendMessage("An unknown error occurred").queue();
				}
			} else if (hook != null && !hook.isExpired()) {
				hook.sendMessage(fe.getMessage()).queue();
			}
		}
	}

	/**
	 * Wrapper class that wraps an AudioPlayer for usage with JDA.
	 */
	private static class LavaPlayerJdaWrapper implements AudioSendHandler {
		private final AudioPlayer audioPlayer;
		private AudioFrame lastFrame;

		public LavaPlayerJdaWrapper(AudioPlayer audioPlayer) {
			this.audioPlayer = audioPlayer;
		}

		@Override
		public boolean canProvide() {
			lastFrame = audioPlayer.provide();
			return lastFrame != null;
		}

		@Override
		public ByteBuffer provide20MsAudio() {
			return ByteBuffer.wrap(lastFrame.getData());
		}

		@Override
		public boolean isOpus() {
			return true;
		}
	}
}
