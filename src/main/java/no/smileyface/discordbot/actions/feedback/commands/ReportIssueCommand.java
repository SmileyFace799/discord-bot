package no.smileyface.discordbot.actions.feedback.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.feedback.ReportIssueAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link ReportIssueAction}.
 */
public class ReportIssueCommand extends ActionCommand<ReportIssueAction.Key> {
	/**
	 * Creates the command.
	 */
	public ReportIssueCommand() {
		super(Commands
				.slash(
						"reportissue",
						"Report an issue with the bot. "
								+ "NB: Make sure to check /knownissues first"
				)
				.addOption(
						OptionType.STRING,
						ReportIssueAction.Key.TOPIC.str(),
						"The topic of your issue",
						true
				).addOption(
						OptionType.STRING,
						ReportIssueAction.Key.DETAILS.str(),
						"The full details of your issue, "
								+ "including instructions on how to reproduce it", true
				)
		);
	}

	@Override
	public Node<ReportIssueAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<ReportIssueAction.Key, Object> args = new Node<>();
		args.addChild(ReportIssueAction.Key.TOPIC, event.getOption(
				ReportIssueAction.Key.TOPIC.str(),
				OptionMapping::getAsString
		));
		args.addChild(ReportIssueAction.Key.DETAILS, event.getOption(
				ReportIssueAction.Key.DETAILS.str(),
				OptionMapping::getAsString
		));
		return args;
	}
}