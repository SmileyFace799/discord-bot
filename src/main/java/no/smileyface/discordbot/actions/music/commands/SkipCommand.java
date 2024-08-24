package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.music.SkipAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link SkipAction}.
 */
public class SkipCommand extends ActionCommand<SkipAction.Key> {
	/**
	 * Creates the command.
	 */
	public SkipCommand() {
		super(Commands
				.slash("skip", "Skips the current song")
				.setGuildOnly(true)
				.addOption(OptionType.INTEGER, SkipAction.Key.AMOUNT.str(),
						"the amount of songs to skip. If invalid, "
								+ "the closest valid value is used  (Default: 1)"
				)
		);
	}

	@Override
	public Node<SkipAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<SkipAction.Key, Object> args = new Node<>();
		args.addChild(SkipAction.Key.AMOUNT, event.getOption(
				SkipAction.Key.AMOUNT.str(),
				1,
				OptionMapping::getAsInt)
		);
		return args;
	}
}