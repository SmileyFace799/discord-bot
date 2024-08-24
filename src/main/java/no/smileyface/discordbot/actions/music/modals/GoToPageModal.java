package no.smileyface.discordbot.actions.music.modals;

import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import no.smileyface.discordbot.actions.music.GoToPageAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionModal;

/**
 * Modal for triggering {@link GoToPageAction}.
 */
public class GoToPageModal extends ActionModal<GoToPageAction.Key> {
	/**
	 * Creates the modal.
	 */
	public GoToPageModal() {
		super("goToPageModal", "Go To Page...", List.of(
				TextInput.create("page", "Page", TextInputStyle.SHORT).build()
		));
	}

	@Override
	public Node<GoToPageAction.Key, Object> getModalArgs(ModalInteractionEvent event) {
		Node<GoToPageAction.Key, Object> args = new Node<>();

		args.addChild(GoToPageAction.Key.PAGE, Objects
				.requireNonNull(event.getValue(GoToPageAction.Key.PAGE.str()))
				.getAsString()
		);

		return args;
	}
}
