package no.smileyface.discordbot.commands;

import java.util.Collection;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
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
	 * Finds a modal based on ID from a collection of modals.
	 *
	 * @param modals The collection of modals
	 * @param modalId The ID of the modal to find
	 * @return The modal with the specified ID, null if not found
	 */
	public static ActionModal<? extends BotAction.ArgKey> findModalById(
			Collection<? extends ActionModal<? extends BotAction.ArgKey>> modals,
			String modalId
	) {
		return modals
				.stream()
				.filter(m -> m.getId().equalsIgnoreCase(modalId))
				.findFirst()
				.orElse(null);
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
	 * @param modals THe collection of possible modals to respond with
	 * @param modalId The ID of the modal to pick for the response
	 */
	public static void tryReplyModal(
			IReplyCallback event,
			Collection<? extends ActionModal<? extends BotAction.ArgKey>> modals,
			String modalId
	) {
		ActionModal<? extends BotAction.ArgKey> modal =
				findModalById(modals, modalId);
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
