package no.smileyface.discordbot.actions.music.modals;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import no.smileyface.discordbot.actions.music.PlayAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionModal;

/**
 * Modal for triggering {@link PlayAction}.
 */
public class PlayModal extends ActionModal<PlayAction.Key> {
	/**
	 * Creates the modal.
	 */
	public PlayModal() {
		super("playModal", "Queue a song / video / playlist", List.of(
				TextInput.create(
						PlayAction.Key.INPUT.str(),
						"URL / YouTube search query",
						TextInputStyle.SHORT
				).build(),
				TextInput.create(
								PlayAction.Key.SONG_SEARCH.str(),
								"Search only for songs? (Default: False)",
								TextInputStyle.SHORT
						).setPlaceholder("y/n, yes/no, true/false (case-insensitive)")
						.setRequired(false)
						.build()
		));
	}

	@Override
	public Node<PlayAction.Key, Object> getModalArgs(ModalInteractionEvent event) {
		Node<PlayAction.Key, Object> args = new Node<>();

		args.addChild(PlayAction.Key.INPUT, Objects.requireNonNull(
				event.getValue(PlayAction.Key.INPUT.str())
		).getAsString());

		ModalMapping songSearch = event.getValue(PlayAction.Key.SONG_SEARCH.str());
		args.addChild(PlayAction.Key.SONG_SEARCH, songSearch != null
				&& Stream.of("y", "yes", "true")
				.anyMatch(yes -> yes.equalsIgnoreCase(songSearch.getAsString()))
		);

		return args;
	}
}