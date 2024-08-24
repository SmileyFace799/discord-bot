package no.smileyface.discordbot.actions.misc.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.misc.SayAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link SayAction}.
 */
public class SayCommand extends ActionCommand<SayAction.Key> {
	/**
	 * Makes the command.
	 */
	public SayCommand() {
		super(Commands
				.slash("say", "Bot will say anything you want")
				.addOption(OptionType.STRING, SayAction.Key.STRING.str(),
						"The string of text the bot should say", true)
		);
	}

	@Override
	public Node<SayAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<SayAction.Key, Object> args = new Node<>();
		args.addChild(SayAction.Key.STRING, event.getOption(
				SayAction.Key.STRING.str(),
				OptionMapping::getAsString
		));
		return args;
	}
}
