package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link no.smileyface.discordbot.actions.music.LeaveAction LeaveAction}.
 */
public class LeaveCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Makes the command.
	 */
	public LeaveCommand() {
		super(
				Commands
						.slash("leave", "Makes the bot leave the voice channel you're in")
						.setGuildOnly(true),
				"stop"
		);
	}
}
