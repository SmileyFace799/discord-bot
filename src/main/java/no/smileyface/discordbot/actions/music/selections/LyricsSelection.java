package no.smileyface.discordbot.actions.music.selections;

import java.util.Collection;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.context.ContextSelection;

/**
 * Selection for triggering
 * {@link no.smileyface.discordbot.actions.music.LyricsAction.LyricsSelectAction
 * LyricsAction.LyricsSelectAction}.
 */
public class LyricsSelection extends ContextSelection<GenericBotAction.ArgKey> {
	private final Collection<String> options;

	/**
	 * Creates the selection.
	 *
	 * @param nextValueKey The key to use for the next selected value in the value node provided by
	 *                     {@link #getSelectionArgs(GenericSelectMenuInteractionEvent)}
	 */
	public LyricsSelection(Collection<String> options, GenericBotAction.ArgKey nextValueKey) {
		super(
				id -> StringSelectMenu
						.create(id)
						.setRequiredRange(1, 1)
						.setPlaceholder("Not the correct lyrics?"),
				nextValueKey
		);
		this.options = options;
	}

	/**
	 * Gets the select menu with the set options.
	 *
	 * @return The select menu with the set options
	 */
	public SelectMenu getSelectWithOptions() {
		return getSelectionMenu(builder -> options
				.forEach(option -> ((StringSelectMenu.Builder) builder)
						.addOption(option, option)
				)
		);
	}
}
