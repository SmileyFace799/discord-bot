package no.smileyface.discordbot.commands.feedback;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Reports an issue with the bot to "me" (bot author).
 */
public class ReportIssueAction extends BotAction<ReportIssueAction.ReportIssueKey> {
	private static final Collection<Long> REPORT_BLACKLIST = Set.of(); //No one, yet :)

	private final Map<Long, LocalDateTime> reportCoolDowns;

	private static class ReportIssueCommand extends ActionCommand<ReportIssueKey> {
		public ReportIssueCommand() {
			super(Commands
					.slash(
							"reportissue",
							"Report an issue with the bot. "
									+ "NB: Make sure to check /knownissues first"
					)
					.addOption(
							OptionType.STRING,
							ReportIssueKey.TOPIC.str(),
							"The topic of your issue",
							true
					).addOption(
							OptionType.STRING,
							ReportIssueKey.DETAILS.str(),
							"The full details of your issue, "
									+ "including instructions on how to reproduce it", true
					)
			);
		}

		@Override
		public MultiTypeMap<ReportIssueKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<ReportIssueKey> args = new MultiTypeMap<>();
			args.put(ReportIssueKey.TOPIC, event.getOption(
					ReportIssueKey.TOPIC.str(),
					OptionMapping::getAsString
			));
			args.put(ReportIssueKey.DETAILS, event.getOption(
					ReportIssueKey.DETAILS.str(),
					OptionMapping::getAsString
			));
			return args;
		}
	}

	/**
	 * Makes the report issue command.
	 */
	public ReportIssueAction() {
		super(new ReportIssueCommand());
		reportCoolDowns = new HashMap<>();
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ReportIssueKey> args,
			InputRecord inputs
	) {
		String topic = args.get(ReportIssueKey.TOPIC, String.class);
		String details = args.get(ReportIssueKey.DETAILS, String.class);

		JDA jda = event.getJDA();
		User author = event.getUser();
		User yorthicc = jda.retrieveUserById(651563251896942602L).complete();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime userReportCoolDown = reportCoolDowns.get(author.getIdLong());
		String response;
		String originalCommand = event instanceof SlashCommandInteractionEvent slashEvent
				? "\n\nIf you need your command again, here it is:\n/"
				+ slashEvent.getName() + " topic:" + topic + " details:" + details
				: null;
		if (REPORT_BLACKLIST.contains(author.getIdLong())) {
			response = "You are blacklisted from reporting issues";
			originalCommand = null;
		} else if (jda.getSelfUser().getIdLong() != yorthicc.getIdLong()) {
			response = "This is not a bot account that belongs to me."
					+ "\nPlease make sure sure the issue still persists with my bot before "
					+ "reporting it (" + yorthicc.getName()
					+ "). If the issue still persists, report it through my bot. "
					+ "If you do not share a server with my bot, "
					+ "message the host of this bot about this issue instead.";
		} else if (userReportCoolDown != null && now.isBefore(userReportCoolDown)) {
			long waitDuration = Duration.between(now, userReportCoolDown).getSeconds();
			response = "You already sent a report within the last hour. "
					+ "To prevent spam & misuse of this command, please wait "
					+ Math.floorDiv(waitDuration, 60) + "m" + (waitDuration % 60)
					+ "s before reporting again.";
		} else {
			User me = jda.retrieveUserById(234724168183054336L).complete();
			me.openPrivateChannel().complete().sendMessage("**New reported issue:**"
					+ "\n\nReported by: " + author.getName()
					+ "\nAuthor ID: " + author.getId()
					+ "\n\n**Topic:**\n" + topic
					+ "\n\n**Details:**\n" + details
			).queue();
			response = "Issue has been reported. I may reach out to you for "
					+ "further details about this issue. If you don't have open DMs, "
					+ "I may send you a friend request (I'm "
					+ me.getName() + ")";
			reportCoolDowns.put(author.getIdLong(), now.plusHours(1));
		}
		if (originalCommand != null) {
			response += originalCommand;
		}
		event.reply(response).setEphemeral(true).queue();
	}

	/**
	 * Keys for args map.
	 */
	public enum ReportIssueKey implements ArgKey {
		TOPIC,
		DETAILS
	}
}
