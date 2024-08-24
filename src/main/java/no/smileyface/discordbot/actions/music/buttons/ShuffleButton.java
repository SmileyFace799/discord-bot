package no.smileyface.discordbot.actions.music.buttons;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;

/**
 * Button for triggering {@link no.smileyface.discordbot.actions.music.ShuffleAction ShuffleAction}.
 */
public class ShuffleButton extends ActionButton<GenericBotAction.ArgKey> {
	/**
	 * Creates the button.
	 */
	public ShuffleButton() {
		super(
				ButtonStyle.PRIMARY,
				"shuffleButton",
				"Shuffle",
				Emoji.fromUnicode("🔀")
		);
	}
}
