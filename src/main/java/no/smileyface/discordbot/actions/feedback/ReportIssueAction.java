package no.smileyface.discordbot.actions.feedback;

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
import no.smileyface.discordbot.actions.feedback.commands.ReportIssueCommand;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Reports an issue with the bot to "me" (bot author).
 */
public class ReportIssueAction extends BotAction<ReportIssueAction.Key> {
	private static final Collection<Long> REPORT_BLACKLIST = Set.of(); //No one, yet :)

	private final Map<Long, LocalDateTime> reportCoolDowns;

	/**
	 * Makes the report issue command.
	 */
	public ReportIssueAction(ActionManager manager) {
		super(manager, new ReportIssueCommand());
		reportCoolDowns = new HashMap<>();
	}

	@Override
	protected void execute(
			IReplyCallback event,
			Node<Key, Object> args
	) {
		String topic = args.getValue(Key.TOPIC, String.class);
		String details = args.getValue(Key.DETAILS, String.class);

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
	public enum Key implements ArgKey {
		TOPIC,
		DETAILS
	}
}
