package no.smileyface.discordbot.actions;

import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Utility methods for actions.
 */
public class ActionUtil {
	private ActionUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * <p>Tries to reply to a modal, and gives an error message if it fails.
	 * This will always acknowledge & reply to the event.</p>
	 * <p>Possible reasons for failure are:</p>
	 * <ul>
	 *     <li>The specified modal ID is not in the given list of modals</li>
	 *     <li>The specified event does not allow for modal replies</li>
	 * </ul>
	 *
	 * @param event The event to reply to
	 * @param inputs THe collection of possible inputs to respond with
	 * @param modalId The ID of the modal to pick for the response
	 */
	public static void tryReplyModal(
			IReplyCallback event,
			InputRecord inputs,
			String modalId
	) {
		ActionModal<? extends BotAction.ArgKey> modal = inputs.findModal(modalId);
		if (modal == null) {
			event.reply(String.format("Could not show modal window \"%s\": "
					+ "The bot has no such modal window", modalId
			)).setEphemeral(true).queue();
		} else if (event instanceof IModalCallback modalCallback) {
			modalCallback.replyModal(modal).queue();
		} else {
			event.reply(String.format("Could not show modal window \"%s\": "
					+ "Event does not allow modal responses", modalId
			)).setEphemeral(true).queue();
		}
	}
}
