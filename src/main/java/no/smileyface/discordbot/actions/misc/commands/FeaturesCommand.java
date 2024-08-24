package no.smileyface.discordbot.actions.misc.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering
 * {@link no.smileyface.discordbot.actions.misc.FeaturesAction FeaturesAction}.
 */
public class FeaturesCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Creates the command.
	 */
	public FeaturesCommand() {
		super(Commands.slash("features",
				"Shows what optional features the bot currently has"
		));
	}
}
