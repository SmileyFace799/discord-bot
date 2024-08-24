package no.smileyface.discordbot.actions.music.buttons;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.actions.music.RepeatAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;

/**
 * Button for triggering {@link RepeatAction}.
 */
public class RepeatButton extends ActionButton<RepeatAction.Key> {
	/**
	 * Creates the button.
	 */
	public RepeatButton() {
		super(
				ButtonStyle.PRIMARY,
				"repeatButton",
				"Repeat Song / Queue / Off",
				Emoji.fromUnicode("🔁")
		);
	}

	@Override
	public Node<RepeatAction.Key, Object> createArgs(ButtonInteractionEvent event) {
		Node<RepeatAction.Key, Object> args = new Node<>();
		args.addChild(RepeatAction.Key.CHANGE_NEXT, new Node<>());
		return args;
	}
}
