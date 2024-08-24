package no.smileyface.discordbot.actions.music;

import core.GLA;
import genius.SongSearch;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.commands.LyricsCommand;
import no.smileyface.discordbot.actions.music.selections.LyricsSelection;
import no.smileyface.discordbot.model.MusicTrack;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.ContextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds lyrics for any song the music bot is playing.
 */
public class LyricsAction extends BotAction<LyricsAction.Key> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LyricsAction.class);

	private final GLA gla;
	private final Pattern removeBrackets;

	/**
	 * Makes the lyrics action.
	 */
	public LyricsAction(ActionManager manager) {
		super(manager, new LyricsCommand());
		this.gla = new GLA();
		this.removeBrackets = Pattern.compile("\\((.*?)\\)|\\[(.*?)]|\\{(.*?)}");
	}

	private static String getQuery(MusicTrack track) {
		String query;
		if (track == null) {
			query = null;
		} else if (track
				.getTitle()
				.toLowerCase()
				.replace(" ", "")
				.contains(track
						.getAuthor()
						.toLowerCase()
						.replace(" ", ""))
		) {
			query = track.getTitle();
		} else {
			query = track.getTitle() + " " + track.getAuthor().replace(" - Topic", "");
		}
		return query;
	}

	private static MessageEmbed makeLyricsEmbed(SongSearch.Hit song) {
		EmbedBuilder lyricsEmbed = new EmbedBuilder()
				.setColor(0xFFFF00)
				.setThumbnail(song.getImageUrl())
				.setAuthor("Provided by Genius", song.getUrl())
				.setFooter(song.getUrl())
				.setTitle(String.format("%s — %s", song.getTitle(), song.getArtist().getName()));
		String lyrics = song.fetchLyrics().replace("&amp;", "&");
		Arrays.stream(lyrics
				.substring(0, Math.min(lyrics.length(), MessageEmbed.EMBED_MAX_LENGTH_BOT - 500))
				.split("\n\n")
		).flatMap(str -> {
			List<String> strParts = new ArrayList<>();
			while (str.length() > MessageEmbed.VALUE_MAX_LENGTH) {
				String part = str
						.substring(0, MessageEmbed.VALUE_MAX_LENGTH + 1)
						.split(" (?=[^ ]*$)")[0];
				strParts.add(part);
				str = str.substring(part.length()).strip();
			}
			strParts.add(str);
			return strParts.stream();
		}).forEach(part -> {
			String[] splitPart = part.split("\n", 2);
			if (splitPart[0].startsWith("[") && splitPart[0].endsWith("]")) {
				if (splitPart.length > 1) {
					lyricsEmbed.addField(splitPart[0], splitPart[1], false);
				} else {
					lyricsEmbed.addField("Lyrics", splitPart[0], false);
				}
			} else {
				lyricsEmbed.addField("Lyrics", part, false);
			}
		});
		return lyricsEmbed.build();
	}

	private void postLyrics(
			InteractionHook hook,
			List<SongSearch.Hit> songs,
			String query,
			MusicTrack track
	) throws IOException {
		if (songs.isEmpty()) {
			songs = gla.search(removeBrackets.matcher(query).replaceAll("")).getHits();
		}
		if (track != null && songs.isEmpty()) {
			query = track.getTitle();
			songs = gla.search(query).getHits();
			if (songs.isEmpty()) {
				songs = gla.search(removeBrackets.matcher(query).replaceAll("")).getHits();
			}
		}
		if (songs.isEmpty()) {
			hook.sendMessage("Could not find lyrics for this song").queue();
		} else {
			try {
				List<MessageEmbed> embeds = songs
						.stream()
						.map(LyricsAction::makeLyricsEmbed)
						.toList();
				hook.sendMessageEmbeds(embeds.getFirst()).addActionRow((
						(LyricsSelection) new LyricsSelectAction(getManager(), embeds)
								.getSelections()
								.stream()
								.findFirst()
								.orElseThrow()
				).getSelectWithOptions()).queue();
			} catch (Exception e) {
				hook.sendMessage("Something went wrong when fetching lyrics").queue();
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	protected void execute(IReplyCallback event, Node<Key, Object> args) {
		MusicTrack track;
		String query;
		if (args.hasChild(Key.SEARCH)) {
			track = null;
			query = args.getValue(Key.SEARCH, String.class);
		} else {
			track = MusicManager.getInstance().getCurrentTrack(event.getGuild());
			query = getQuery(track);
		}
		if (query == null) {
			event.reply("The bot must either be playing something, "
					+ "or you must provide a song to search for"
			).setEphemeral(true).queue();
		} else {
			event.deferReply().setEphemeral(true).submit().thenAccept(hook -> {
				try {
					postLyrics(hook, gla.search(query).getHits(), query, track);
				} catch (IOException ioe) {
					hook.sendMessage(ioe.getMessage()).queue();
				}
			});
		}
	}

	/**
	 * Selects one of multiple songs to show lyrics for.
	 */
	public static class LyricsSelectAction extends ContextAction<ArgKey> {
		private static final ArgKey NEXT_VALUE_KEY = null;

		private final List<MessageEmbed> embeds;

		/**
		 * Creates the action.
		 *
		 * @param manager The {@link ActionManager} of the bot
		 * @param embeds  The list of lyric embeds for the user to choose between
		 */
		public LyricsSelectAction(ActionManager manager, List<MessageEmbed> embeds) {
			super(manager, Duration.ofMinutes(15), false);
			this.embeds = embeds;
			addSelections(new LyricsSelection(
					embeds.stream().map(MessageEmbed::getTitle).toList(),
					NEXT_VALUE_KEY)
			);
		}

		@Override
		protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
			String songTitle = args.getChild(NEXT_VALUE_KEY).getValue(String.class);
			MessageEmbed embed = embeds
					.stream()
					.filter(e -> songTitle.equals(e.getTitle()))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("Unable to load those lyrics"));
			if (event instanceof GenericComponentInteractionCreateEvent interactionEvent) {
				interactionEvent.deferEdit().queue();
				event.getHook().editOriginalEmbeds(embed).queue();
			} else {
				event.replyEmbeds(embed).setEphemeral(true).queue();
			}
		}
	}

	/**
	 * Keys for args map.
	 */
	public enum Key implements ArgKey {
		SEARCH
	}
}
