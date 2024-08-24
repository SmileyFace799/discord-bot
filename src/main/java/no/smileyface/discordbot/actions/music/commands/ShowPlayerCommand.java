package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering
 * {@link no.smileyface.discordbot.actions.music.ShowPlayerAction ShowPlayerAction}.
 */
public class ShowPlayerCommand extends ActionCommand<GenericBotAction.ArgKey> {
	/**
	 * Creates the command.
	 */
	public ShowPlayerCommand() {
		super(Commands
				.slash("showplayer", "Shows the audio player")
				.setGuildOnly(true)
		);
	}
}
