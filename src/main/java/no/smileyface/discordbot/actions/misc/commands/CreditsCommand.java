package no.smileyface.discordbot.actions.misc.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link no.smileyface.discordbot.actions.misc.CreditsAction CreditsAction}.
 */
public class CreditsCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Makes the command.
	 */
	public CreditsCommand() {
		super(Commands.slash("credits", "Shows bot credits"));
	}
}
