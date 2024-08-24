package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link no.smileyface.discordbot.actions.music.JoinAction JoinAction}.
 */
public class JoinCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Creates the command.
	 */
	public JoinCommand() {
		super(Commands.slash("join", "Joins a voice channel").setGuildOnly(true));
	}
}
