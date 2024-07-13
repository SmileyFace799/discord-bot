package no.smileyface.discordbot.actions.music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import no.smileyface.discordbot.checks.InVoice;
import no.smileyface.discordbot.model.MusicTrack;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbot.model.querying.Query;
import no.smileyface.discordbot.model.querying.QueryError;
import no.smileyface.discordbot.model.querying.QueryParser;
import no.smileyface.discordbot.model.querying.QueryUtil;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.ContextButton;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Plays music.
 */
public class PlayAction extends BotAction<PlayAction.PlayKey> {

	private final QueryParser queryParser;

	private static class PlayCommand extends ActionCommand<PlayKey> {
		public PlayCommand() {
			super(
					Commands
							.slash("play", "Plays a song")
							.setGuildOnly(true)
							.addOption(
									OptionType.STRING,
									PlayKey.INPUT.toString().toLowerCase(),
									"A search or URL for the song to play. "
											+ "Search is done through YouTube", true)
							.addOption(
									OptionType.BOOLEAN,
									PlayKey.SONG_SEARCH.toString().toLowerCase(),
									"If the YouTube search should search for songs only. "
											+ "Ignored if input is a URL  (Default: False)"),
					"p"
			);
		}

		@Override
		public MultiTypeMap<PlayKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<PlayKey> args = new MultiTypeMap<>();
			args.put(PlayKey.INPUT, event.getOption(
					PlayKey.INPUT.str(),
					OptionMapping::getAsString)
			);
			args.put(PlayKey.SONG_SEARCH, event.getOption(
					PlayKey.SONG_SEARCH.str(),
					false,
					OptionMapping::getAsBoolean
			));
			return args;
		}
	}

	private static class PlayModal extends ActionModal<PlayKey> {
		public PlayModal() {
			super("queueSongModal", "Queue a song / video / playlist", List.of(
					TextInput.create(
							PlayKey.INPUT.str(),
							"URL / YouTube search query",
							TextInputStyle.SHORT
					).build(),
					TextInput.create(
									PlayKey.SONG_SEARCH.str(),
									"Search only for songs? (Default: False)",
									TextInputStyle.SHORT
							).setPlaceholder("y/n, yes/no, true/false (case-insensitive)")
							.setRequired(false)
							.build()
			));
		}

		@Override
		public MultiTypeMap<PlayKey> getModalArgs(ModalInteractionEvent event) {
			MultiTypeMap<PlayKey> args = new MultiTypeMap<>();

			args.put(PlayKey.INPUT, Objects.requireNonNull(
					event.getValue(PlayKey.INPUT.str())
			).getAsString());

			ModalMapping songSearch = event.getValue(PlayKey.SONG_SEARCH.str());
			args.put(PlayKey.SONG_SEARCH, songSearch != null
					&& Stream.of("y", "yes", "true")
					.anyMatch(yes -> yes.equalsIgnoreCase(songSearch.getAsString()))
			);

			return args;
		}
	}

	/**
	 * Button to undo queuing tracks action.
	 */
	public class UndoContextButton extends ContextButton<PlayKey> {
		private final List<MusicTrack> tracksToUndo;

		protected UndoContextButton() {
			super(PlayAction.this, ButtonStyle.PRIMARY, "Undo");
			this.tracksToUndo = new ArrayList<>();
		}

		public void addTracksToUndo(List<MusicTrack> tracks) {
			tracksToUndo.addAll(tracks);
		}

		public void addTracksToUndo(MusicTrack track) {
			tracksToUndo.add(track);
		}

		@Override
		public void clicked(
				ButtonInteractionEvent event,
				MultiTypeMap<PlayKey> args,
				InputRecord inputs
		) {
			try {
				MusicManager.getInstance().remove(
						tracksToUndo,
						Objects.requireNonNull(event.getGuild())
				);
				event.reply("Removed the queued song" + (tracksToUndo.size() == 1 ? "" : "s"))
						.setEphemeral(true)
						.queue();
			} catch (NullPointerException npe) {
				event.reply("Music has already stopped")
						.setEphemeral(true)
						.queue();
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			if (!super.equals(o)) {
				return false;
			}
			UndoContextButton that = (UndoContextButton) o;
			return Objects.equals(tracksToUndo, that.tracksToUndo);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), tracksToUndo);
		}
	}

	/**
	 * Makes the play action.
	 */
	public PlayAction(QueryParser queryParser) {
		super(new PlayCommand(), new PlayModal(), new InVoice());
		this.queryParser = queryParser;
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<PlayKey> args,
			InputRecord inputs
	) {
		event.deferReply().setEphemeral(true).queue();
		Member author = Objects.requireNonNull(event.getMember());
		String input = args.get(PlayKey.INPUT, String.class);
		boolean songSearch = args.get(PlayKey.SONG_SEARCH, Boolean.class);
		List<Query> queries;

		String[] splitInput = input.replace("  ", " ").split(" ");
		if (Arrays.stream(splitInput).allMatch(identifier ->
				identifier.startsWith("https://") || identifier.startsWith("http://"))
		) {
			queries = Arrays.stream(splitInput)
					.flatMap(identifier -> queryParser.getQueries(identifier).stream())
					.collect(Collectors.toCollection(ArrayList::new));
		} else {
			String search = input;

			if (songSearch) {
				search += QueryUtil.YOUTUBE_SONG_FILTER;
			}
			queries = new ArrayList<>(queryParser.getQueries(search));
		}

		MusicManager.getInstance().createPlayerIfNotExists(
				Objects.requireNonNull(Objects.requireNonNull(
						author.getVoiceState()
				).getChannel()),
				(GuildMessageChannel) event.getMessageChannel(),
				inputs,
				null
		);

		InteractionHook hook = event.getHook();
		if (queries.size() == 1) {
			Query query = queries.getFirst();
			if (query instanceof QueryError error) {
				hook.sendMessage("Unable to parse input: " + error.errorMessage())
						.setEphemeral(true)
						.queue();
			} else {
				MusicManager.getInstance().queue(
						queries.getFirst().str(),
						new UndoContextButton(),
						author,
						hook
				);
			}
		} else {
			List<Query> queryErrors = queries
					.stream()
					.filter(QueryError.class::isInstance)
					.toList();
			queries.removeAll(queryErrors);
			MusicManager.getInstance().queueMultiple(
					queries.stream().map(Query::str).toList(),
					new UndoContextButton(),
					author,
					hook,
					queryErrors.size()
			);
		}
	}

	/**
	 * Keys for args map.
	 */
	public enum PlayKey implements ArgKey {
		INPUT,
		SONG_SEARCH
	}
}
