package no.smileyface.discordbot.commands.music;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionModal;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Navigates the music player to a specific page.
 */
public class GoToPageAction extends BotAction<GoToPageAction.GoToPageKey> {
	private static final String SHOWING_PAGE_BASE = "Showing page %s!";

	private static class PrevPageButton extends ActionButton<GoToPageKey> {
		/**
		 * Makes the prev page action.
		 */
		public PrevPageButton() {
			super(
					ButtonStyle.PRIMARY,
					"prevPageButton",
					"Previous Page",
					Emoji.fromUnicode("◀")
			);
		}

		@Override
		public MultiTypeMap<GoToPageKey> createArgs(ButtonInteractionEvent event) {
			MultiTypeMap<GoToPageKey> args = new MultiTypeMap<>();
			args.put(GoToPageKey.CHANGE, false);
			return args;
		}
	}


	private static class NextPageButton extends ActionButton<GoToPageKey> {
		/**
		 * Makes the next page action.
		 */
		public NextPageButton() {
			super(
					ButtonStyle.PRIMARY,
					"nextPageButton",
					"Next Page",
					Emoji.fromUnicode("▶")
			);
		}

		@Override
		public MultiTypeMap<GoToPageKey> createArgs(ButtonInteractionEvent event) {
			MultiTypeMap<GoToPageKey> args = new MultiTypeMap<>();
			args.put(GoToPageKey.CHANGE, true);
			return args;
		}
	}

	private static class GoToPageModal extends ActionModal<GoToPageKey> {
		/**
		 * Makes the modal.
		 */
		public GoToPageModal() {
			super("goToPageModal", "Go To Page...", List.of(
					TextInput.create("page", "Page", TextInputStyle.SHORT).build()
			));
		}

		@Override
		public MultiTypeMap<GoToPageKey> getModalArgs(ModalInteractionEvent event) {
			MultiTypeMap<GoToPageKey> args = new MultiTypeMap<>();

			args.put(GoToPageKey.PAGE, Objects.requireNonNull(
					event.getValue(GoToPageKey.PAGE.str())
			).getAsString());

			return args;
		}
	}

	/**
	 * Makes the go to page action.
	 */
	public GoToPageAction() {
		super(
				null,
				Set.of(new PrevPageButton(), new NextPageButton()),
				Set.of(new GoToPageModal()),
				new InVoiceWithBot()
		);
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<GoToPageKey> args,
			InputRecord inputs
	) {
		if (args.containsKey(GoToPageKey.CHANGE)) {
			boolean next = args.get(GoToPageKey.CHANGE, Boolean.class);
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
			String rawPage = args.get(GoToPageKey.PAGE, String.class);
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
	public enum GoToPageKey implements ArgKey {
		PAGE,
		CHANGE
	}
}
