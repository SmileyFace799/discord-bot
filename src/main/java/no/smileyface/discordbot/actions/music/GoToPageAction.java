package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.buttons.GoToPageButton;
import no.smileyface.discordbot.actions.music.buttons.ShiftPageButton;
import no.smileyface.discordbot.actions.music.modals.GoToPageModal;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Navigates the music player to a specific page.
 */
public class GoToPageAction extends BotAction<GoToPageAction.Key> {
	private static final String SHOWING_PAGE_BASE = "Showing page %s!";

	private final BotAction<ArgKey> goToPageModalCreator;

	/**
	 * Makes the "go to page" action.
	 */
	public GoToPageAction(ActionManager manager) {
		super(manager, new InVoiceWithBot());
		addButtons(new ShiftPageButton.Previous(), new ShiftPageButton.Next());
		GoToPageModal goToPageModal = new GoToPageModal();
		addModals(goToPageModal);
		this.goToPageModalCreator =
				BotAction.respondWithModal(goToPageModal, manager, new InVoiceWithBot());
		goToPageModalCreator.addButtons(new GoToPageButton());
	}

	public BotAction<ArgKey> getModalCreator() {
		return goToPageModalCreator;
	}

	@Override
	protected void execute(IReplyCallback event, Node<Key, Object> args) {
		if (args.hasChild(Key.CHANGE)) {
			boolean next = args.getValue(Key.CHANGE, Boolean.class);
			if (next) {
				MusicManager.getInstance().incrementPage(
						Objects.requireNonNull(event.getGuild()),
						(actualPage, changed) -> event.reply(Boolean.TRUE.equals(changed)
								? String.format(SHOWING_PAGE_BASE, actualPage)
								: "Already showing the last page"
						).setEphemeral(true).queue()
				);
			} else {
				MusicManager.getInstance().decrementPage(
						Objects.requireNonNull(event.getGuild()),
						(actualPage, changed) -> event.reply(Boolean.TRUE.equals(changed)
								? String.format(SHOWING_PAGE_BASE, actualPage)
								: "Already showing the first page"
						).setEphemeral(true).queue()
				);
			}
		} else {
			String rawPage = args.getValue(Key.PAGE, String.class);
			try {
				MusicManager.getInstance().setPage(
						Objects.requireNonNull(event.getGuild()),
						Integer.parseInt(rawPage),
						actualPage -> event
								.reply(String.format(SHOWING_PAGE_BASE, actualPage))
								.setEphemeral(true)
								.queue()
				);
			} catch (NumberFormatException nfe) {
				event.reply(String.format("\"%s\" is not a valid number", rawPage))
						.setEphemeral(true).queue();
			}
		}
	}

	/**
	 * Argument keys for {@link GoToPageAction}.
	 */
	public enum Key implements ArgKey {
		PAGE,
		CHANGE
	}
}
