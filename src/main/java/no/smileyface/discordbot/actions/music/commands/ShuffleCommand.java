package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering
 * {@link no.smileyface.discordbot.actions.music.ShuffleAction ShuffleAction}.
 */
public class ShuffleCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Creates the command.
	 */
	public ShuffleCommand() {
		super(
				Commands
						.slash("shuffle", "Toggles shuffle")
						.setGuildOnly(true),
				"shf"
		);
	}
}
