package no.smileyface.discordbot.actions.misc.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.misc.NotifyAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command to trigger {@link NotifyAction}.
 */
public class NotifyCommand extends ActionCommand<NotifyAction.Key> {
	/**
	 * Makes the command.
	 */
	public NotifyCommand() {
		super(Commands
				.slash("notify", "Allows you to be notified about updates & expected downtime")
				.addOption(
						OptionType.BOOLEAN,
						NotifyAction.Key.NOTIFY_ME.str(),
						"If you wish to be notified or not. "
								+ "Leave blank to view current notify status"
				)
		);
	}

	@Override
	public Node<NotifyAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<NotifyAction.Key, Object> args = new Node<>();

		OptionMapping notifyMeMapping = event.getOption(NotifyAction.Key.NOTIFY_ME.str());
		if (notifyMeMapping != null) {
			args.addChild(NotifyAction.Key.NOTIFY_ME, notifyMeMapping.getAsBoolean());
		}

		return args;
	}
}
