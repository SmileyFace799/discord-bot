package no.smileyface.discordbot.actions.feedback.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering
 * {@link no.smileyface.discordbot.actions.feedback.KnownIssuesAction KnownIssuesAction}.
 */
public class KnownIssuesCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Creates the command.
	 */
	public KnownIssuesCommand() {
		super(Commands.slash(
				"knownissues",
				"A list of known issues with the bot that will not be fixed"
		));
	}
}
