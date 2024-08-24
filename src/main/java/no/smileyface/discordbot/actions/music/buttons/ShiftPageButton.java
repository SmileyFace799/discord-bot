package no.smileyface.discordbot.actions.music.buttons;

import java.util.Objects;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.actions.music.GoToPageAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionButton;

/**
 * Superclass for {@link Previous} and {@link Next}.
 */
public sealed class ShiftPageButton extends ActionButton<GoToPageAction.Key> {
	private final boolean shiftNext;

	/**
	 * Creates the button.
	 *
	 * @param shiftNext If the button should shift forwards
	 */
	private ShiftPageButton(boolean shiftNext) {
		super(
				ButtonStyle.PRIMARY,
				shiftNext ? "nextPageButton" : "prevPageButton",
				shiftNext ? "Next Page" : "Previous Page",
				Emoji.fromUnicode(shiftNext ? "▶" : "◀")
		);
		this.shiftNext = shiftNext;
	}

	@Override
	public Node<GoToPageAction.Key, Object> createArgs(ButtonInteractionEvent event) {
		Node<GoToPageAction.Key, Object> args = new Node<>();
		args.addChild(GoToPageAction.Key.CHANGE, shiftNext);
		return args;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		ShiftPageButton that = (ShiftPageButton) o;
		return shiftNext == that.shiftNext;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), shiftNext);
	}

	/**
	 * Button for triggering {@link GoToPageAction}.
	 */
	public static non-sealed class Previous extends ShiftPageButton {
		/**
		 * Creates the button.
		 */
		public Previous() {
			super(false);
		}
	}

	/**
	 * Button for triggering {@link GoToPageAction}.
	 */
	public static non-sealed class Next extends ShiftPageButton {
		/**
		 * Creates the button.
		 */
		public Next() {
			super(true);
		}
	}
}
