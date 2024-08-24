package no.smileyface.discordbot.actions.music.buttons;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.actions.music.SkipAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;

/**
 * Button for triggering {@link SkipAction}.
 */
public class SkipButton extends ActionButton<SkipAction.Key> {
	/**
	 * Creates the button.
	 */
	public SkipButton() {
		super(ButtonStyle.PRIMARY, "skipButton", "Skip", Emoji.fromUnicode("⏩"));
	}

	@Override
	public Node<SkipAction.Key, Object> createArgs(ButtonInteractionEvent event) {
		Node<SkipAction.Key, Object> args = new Node<>();
		args.addChild(SkipAction.Key.AMOUNT, 1);
		return args;
	}
}
