package no.smileyface.discordbot.view;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.smileyface.discordbot.model.MusicTrack;
import no.smileyface.discordbot.model.intermediary.QueueEventListener;
import no.smileyface.discordbot.model.TrackQueue;
import no.smileyface.discordbot.model.intermediary.events.BotJoinedEvent;
import no.smileyface.discordbot.model.intermediary.events.ChangePageEvent;
import no.smileyface.discordbot.model.intermediary.events.MultipleQueuedEvent;
import no.smileyface.discordbot.model.intermediary.events.PauseChangedEvent;
import no.smileyface.discordbot.model.intermediary.events.PlayerStoppedEvent;
import no.smileyface.discordbot.model.intermediary.events.PlaylistQueuedEvent;
import no.smileyface.discordbot.model.intermediary.events.QueueEvent;
import no.smileyface.discordbot.model.intermediary.events.RepeatChangedEvent;
import no.smileyface.discordbot.model.intermediary.events.SetPageEvent;
import no.smileyface.discordbot.model.intermediary.events.ShowPlayerEvent;
import no.smileyface.discordbot.model.intermediary.events.ShuffleChangedEvent;
import no.smileyface.discordbot.model.intermediary.events.TrackQueuedEvent;
import no.smileyface.discordbot.model.intermediary.events.TrackSkippedEvent;
import no.smileyface.discordbot.model.intermediary.events.TracksRemovedEvent;
import no.smileyface.discordbotframework.Identifier;

public class TrackQueueMessage implements QueueEventListener {
	private static final int QUEUE_PAGE_SIZE = 10;

	private final TrackQueue queue;
	private final Identifier identifier;

	private Message playerMessage;
	private String lastCommand;
	private boolean musicEnded;
	private boolean musicPaused;
	private int page;
	private int lastPage;

	public TrackQueueMessage(TrackQueue queue, Identifier identifier) {
		this.queue = queue;
		this.identifier = identifier;

		this.musicEnded = false;
		this.musicPaused = false;
		this.page = 1;
		this.lastPage = 1;
	}

	/**
	 * Builds an embed with the current audio information.
	 *
	 * @return The constructed embed
	 */
	private List<MessageEmbed> buildEmbeds() {
		MusicTrack current = queue.getCurrentlyPlaying();
		EmbedBuilder playerEmbed = new EmbedBuilder();
		EmbedBuilder queueEmbed = new EmbedBuilder();
		List<EmbedBuilder> showEmbeds = new ArrayList<>();
		showEmbeds.add(playerEmbed);
		if (musicEnded) {
			playerEmbed.setColor(0xFF0000).setTitle("Music has ended");
		} else if (current == null) {
			playerEmbed.setColor(0xFFFF00).setTitle("Ready to play music...");
		} else {
			if (musicPaused) {
				playerEmbed
						.setColor(0xFFFF00)
						.setTitle("PAUSED");
			} else {
				playerEmbed
						.setColor(0x00FF00)
						.setTitle("PLAYING MUSIC");
			}
			playerEmbed
					.setDescription("**NOW PLAYING:**")
					.addField("Title:", current.getTitle(), false)
					.addField("Uploaded by:", current.getAuthor(), false)
					.addField("Queued by:", current.getQueuedBy().getEffectiveName(), false)
					.addField("Link:", current.getLink(), false);

			//Make & show queueEmbed
			showEmbeds.add(queueEmbed);
			List<MusicTrack> queuedTracks = new ArrayList<>();
			queuedTracks.add(current);
			queuedTracks.addAll(queue.getTracks());
			lastPage = Math.floorDiv(queuedTracks.size() - 1, QUEUE_PAGE_SIZE) + 1;
			if (page > lastPage) {
				this.page = lastPage;
			}
			queueEmbed
					.setColor(0x7F7F7F)
					.setTitle("QUEUE")
					.setDescription(String.format("Shuffle: **%s**%nRepeat: **%s**",
							queue.isShuffled() ? "Yes" : "No",
							queue.getRepeat().getStr()
					))
					.addField(
							String.format("Page %s of %s", page, lastPage),
							String.join("\n",
									queuedTracks.subList(
													(page - 1) * QUEUE_PAGE_SIZE,
													Math.min(
															page * QUEUE_PAGE_SIZE,
															queuedTracks.size()
													)
											)
											.stream()
											.map(track -> "  **"
													+ (track.equals(queue.getCurrentlyPlaying())
													? "Playing"
													: (queuedTracks.indexOf(track) + 1)) + ":** "
													+ track.getTitle()
											)
											.toList()
							), false);
		}
		if (lastCommand != null) {
			playerEmbed.setFooter("Last command - " + lastCommand);
		}

		return showEmbeds.stream().map(EmbedBuilder::build).toList();
	}

	/**
	 * Updates the player message, and makes it if it doesn't exist yet.
	 *
	 * @param repost If the player message is already posted,
	 *               repost the message instead of editing it
	 */
	private void updateEmbed(boolean repost) {
		List<MessageEmbed> embeds = buildEmbeds();

		if (repost && playerMessage != null) {
			playerMessage.delete().queue();
			playerMessage = null;
		}

		if (playerMessage == null) {
			playerMessage = queue
					.getPlayerChannel()
					.sendMessageEmbeds(embeds)
					.addActionRow(
							identifier.findButton("skipButton").orElseThrow(),
							identifier.findButton("playPauseButton").orElseThrow(),
							identifier.findButton("queueButton").orElseThrow(),
							identifier.findButton("stopButton").orElseThrow()
					).addActionRow(
							identifier.findButton("prevPageButton").orElseThrow(),
							identifier.findButton("nextPageButton").orElseThrow(),
							identifier.findButton("shuffleButton").orElseThrow(),
							identifier.findButton("repeatButton").orElseThrow(),
							identifier.findButton("goToPageButton").orElseThrow()
					).complete();
		} else {
			playerMessage.editMessageEmbeds(embeds).queue();
			if (musicEnded) {
				playerMessage.editMessageComponents(Collections.emptyList()).queue();
			}
		}
	}

	/**
	 * Sets the footer with the last command used.
	 *
	 * @param author      The author of that command
	 * @param lastCommand A description of what the last command was
	 */
	private void setLastCommand(Member author, String lastCommand) {
		this.lastCommand = author.getEffectiveName() + ": " + lastCommand;
	}

	/**
	 * Sets the current page. If the new page is an invalid page,
	 * the nearest valid page will be shown.
	 *
	 * @param page The page to go to
	 * @return If actual new page
	 */
	private int setPage(int page) {
		this.page = Math.clamp(page, 1, lastPage);
		return this.page;
	}

	/**
	 * Changes the page by incrementing or decrementing it
	 *
	 * @param increment If the page should increment. Will decrement otherwise
	 * @return If the change actually changed the page
	 */
	private int incrementPage(boolean increment) {
		return setPage(page + (increment ? 1 : -1));
	}

	@Override
	public void onMusicEvent(QueueEvent event) {
		boolean repostMessage = false;
		switch (event) {
			case BotJoinedEvent(Member createdBy) -> setLastCommand(
					createdBy,
					"Joined the voice channel"
			);
			case TrackQueuedEvent(MusicTrack track) -> setLastCommand(
					track.getQueuedBy(),
					String.format("Queued \"%s\"", track.getTitle())
			);
			case PlaylistQueuedEvent(AudioPlaylist playlist, Member queuedBy) -> setLastCommand(
					queuedBy,
					String.format("Queued \"%s\"", playlist.getName())
			);
			case MultipleQueuedEvent(Member queuedBy) -> setLastCommand(
					queuedBy,
					"Queued multiple songs / videos"
			);
			case TracksRemovedEvent(Member removedBy) -> setLastCommand(
					removedBy,
					"Removed songs / videos from the queue"
			);
			case PauseChangedEvent(boolean paused, Member pausedBy) -> {
				musicPaused = paused;
				setLastCommand(
						pausedBy,
						String.format("%s the music", paused ? "Paused" : "Resumed")
				);
			}
			case ShuffleChangedEvent(boolean shuffled, Member shuffledBy) -> setLastCommand(
					shuffledBy,
					String.format("%s the queue", shuffled ? "Shuffled" : "Un-shuffled")
			);
			case RepeatChangedEvent(TrackQueue.Repeat repeat, Member changedBy) -> setLastCommand(
					changedBy,
					switch (repeat) {
						case NO_REPEAT -> "Turned off repeat";
						case REPEAT_SONG -> "Turned on repeat for the current song";
						case REPEAT_QUEUE -> "Turned on repeat for the queue";
					}
			);
			case TrackSkippedEvent(Member skippedBy, int amount) -> setLastCommand(
					skippedBy,
					String.format("Skipped %s song%s",
							amount,
							amount == 1 ? "" : "s"
					)
			);
			case PlayerStoppedEvent(Member stoppedBy) -> {
				musicEnded = true;
				if (stoppedBy != null) {
					setLastCommand(
							stoppedBy,
							"Stopped the music, and made the bot leave voice channel"
					);
				}
			}
			case SetPageEvent(int p, IntConsumer callback) -> callback.accept(setPage(p));
			case ChangePageEvent(boolean increment, BiConsumer<Integer, Boolean> callback) -> {
				int oldPage = page;
				int newPage = incrementPage(increment);
				callback.accept(newPage, oldPage != newPage);
			}
			case ShowPlayerEvent() -> repostMessage = true;
			default -> {
				// Do nothing
			}
		}
		updateEmbed(repostMessage);
	}
}
