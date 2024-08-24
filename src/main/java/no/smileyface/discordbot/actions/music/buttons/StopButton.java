package no.smileyface.discordbot.actions.music.buttons;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;

/**
 * Button for triggering {@link no.smileyface.discordbot.actions.music.LeaveAction LeaveAction}.
 */
public class StopButton extends ActionButton<GenericBotAction.ArgKey> {
	/**
	 * Makes the button.
	 */
	public StopButton() {
		super(ButtonStyle.DANGER, "stopButton", "Stop");
	}
}
