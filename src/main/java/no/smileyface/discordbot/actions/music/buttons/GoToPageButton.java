package no.smileyface.discordbot.actions.music.buttons;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;

/**
 * Button for creating
 * {@link no.smileyface.discordbot.actions.music.modals.GoToPageModal GoToPageModal}.
 */
public class GoToPageButton extends ActionButton<GenericBotAction.ArgKey> {
	/**
	 * Creates the button.
	 */
	public GoToPageButton() {
		super(ButtonStyle.SECONDARY, "goToPageButton", "Go To Page");
	}
}
