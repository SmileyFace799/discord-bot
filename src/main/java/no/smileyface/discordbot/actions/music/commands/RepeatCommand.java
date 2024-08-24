package no.smileyface.discordbot.actions.music.commands;

import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.music.RepeatAction;
import no.smileyface.discordbot.model.TrackQueue;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link RepeatAction}.
 */
public class RepeatCommand extends ActionCommand<RepeatAction.Key> {
	/**
	 * Creates the command.
	 */
	public RepeatCommand() {
		super(
				Commands
						.slash("repeat", "Changes the repeat mode")
						.addOption(OptionType.STRING, RepeatAction.Key.REPEAT_MODE.str(),
								"The repeat mode to set. Must be \"song\", "
										+ "\"queue\" or \"off\"", true)
						.setGuildOnly(true),
				"rpt"
		);
	}

	@Override
	public Node<RepeatAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<RepeatAction.Key, Object> args = new Node<>();
		args.addChild(
				RepeatAction.Key.REPEAT_MODE,
				event.getOption(RepeatAction.Key.REPEAT_MODE.str(),
						(Function<? super OptionMapping, ? extends TrackQueue.Repeat>)
								repeatStr -> {
									TrackQueue.Repeat repeat;
									try {
										repeat = TrackQueue.Repeat.getRepeat(repeatStr.getAsString()
												.replace("\"", ""));
									} catch (IllegalArgumentException iae) {
										repeat = null;
									}
									return repeat;
								}
				)
		);
		return args;
	}
}