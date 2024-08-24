package no.smileyface.discordbot.actions.music;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.actions.music.buttons.QueueMoreButton;
import no.smileyface.discordbot.actions.music.commands.PlayCommand;
import no.smileyface.discordbot.actions.music.modals.PlayModal;
import no.smileyface.discordbot.checks.InVoice;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.MusicTrack;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbot.model.querying.Query;
import no.smileyface.discordbot.model.querying.QueryError;
import no.smileyface.discordbot.model.querying.QueryParser;
import no.smileyface.discordbot.model.querying.QueryUtil;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.ContextAction;
import no.smileyface.discordbotframework.entities.context.ContextButton;

/**
 * Plays music.
 */
public class PlayAction extends BotAction<PlayAction.Key> {
	private final QueryParser queryParser;
	private final BotAction<ArgKey> playModalCreator;

	/**
	 * Action to undo queuing tracks action.
	 */
	public static class UndoContextAction extends ContextAction<Key> {
		private final List<MusicTrack> tracksToUndo;

		private UndoContextAction(ActionManager manager) {
			super(manager, Duration.ofMinutes(15), true);
			this.tracksToUndo = new ArrayList<>();
			addButtons(new ContextButton<>(ButtonStyle.PRIMARY, "Undo"));
		}

		public void addTracksToUndo(List<MusicTrack> tracks) {
			tracksToUndo.addAll(tracks);
		}

		public void addTracksToUndo(MusicTrack track) {
			tracksToUndo.add(track);
		}

		@Override
		public void execute(
				IReplyCallback event,
				Node<Key, Object> args
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
	}

	/**
	 * Makes the play action.
	 */
	public PlayAction(ActionManager manager, QueryParser queryParser) {
		super(manager, new PlayCommand(), new InVoice());
		this.queryParser = queryParser;
		PlayModal playModal = new PlayModal();
		addModals(playModal);
		this.playModalCreator =
				BotAction.respondWithModal(playModal, manager, new InVoiceWithBot());
		playModalCreator.addButtons(new QueueMoreButton());
	}

	public BotAction<ArgKey> getModalCreator() {
		return playModalCreator;
	}

	@Override
	protected void execute(IReplyCallback event, Node<Key, Object> args) {
		event.deferReply().setEphemeral(true).queue();
		Member author = Objects.requireNonNull(event.getMember());
		String input = args.getValue(Key.INPUT, String.class);
		boolean songSearch = args.getValue(Key.SONG_SEARCH, Boolean.class);
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
				getIdentifier(),
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
						new UndoContextAction(getManager()),
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
					new UndoContextAction(getManager()),
					author,
					hook,
					queryErrors.size()
			);
		}
	}

	/**
	 * Keys for args map.
	 */
	public enum Key implements ArgKey {
		INPUT,
		SONG_SEARCH
	}
}
