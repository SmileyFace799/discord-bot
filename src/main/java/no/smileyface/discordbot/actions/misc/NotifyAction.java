package no.smileyface.discordbot.actions.misc;

import java.io.IOException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.files.AnnouncementWhitelist;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.files.LongCollectionFileInterface;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Allows the user to be notified about updates & expected downtime.
 */
public class NotifyAction extends BotAction<NotifyAction.NotifyKey> {
	private static class NotifyCommand extends ActionCommand<NotifyKey> {
		public NotifyCommand() {
			super(Commands
					.slash("notify", "Allows you to be notified about updates & expected downtime")
					.addOption(
							OptionType.BOOLEAN,
							NotifyKey.NOTIFY_ME.str(),
							"If you wish to be notified or not. "
									+ "Leave blank to view current notify status"
					)
			);
		}

		@Override
		public MultiTypeMap<NotifyKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<NotifyKey> args = new MultiTypeMap<>();

			OptionMapping notifyMeMapping = event.getOption(NotifyKey.NOTIFY_ME.str());
			if (notifyMeMapping != null) {
				args.put(NotifyKey.NOTIFY_ME, notifyMeMapping.getAsBoolean());
			}

			return args;
		}
	}

	public NotifyAction() {
		super(new NotifyCommand());
	}

	@Override
	protected void execute(IReplyCallback event, MultiTypeMap<NotifyKey> args, InputRecord inputs) {
		String response;
		long id = event.getUser().getIdLong();
		try {
			LongCollectionFileInterface announcementWhitelist = AnnouncementWhitelist.getInstance();
			boolean notifyOld = announcementWhitelist.contains(id);
			if (args.containsKey(NotifyKey.NOTIFY_ME)) {
				boolean notifyNew = args.get(NotifyKey.NOTIFY_ME, Boolean.class);
				if (!notifyOld && notifyNew) {
					announcementWhitelist.addChecked(id);
					response = "You will be notified from now on!";

				} else if (notifyOld && !notifyNew) {
					announcementWhitelist.removeChecked(id);
					response = "You will no longer be notified from now on!";
				} else {
					response = String.format("You're already%s being notified",
							notifyNew ? "" : " not"
					);
				}
			} else {
				response = String.format("You %s being notified!",
						notifyOld ? "**are**" : "are **not**"
				);
			}
		} catch (IOException ioe) {
			response = "There was an error setting your notify status. "
					+ "Please contact the bot owner";
		}
		event.reply(response).setEphemeral(true).queue();
	}

	/**
	 * Keys for args map.
	 */
	public enum NotifyKey implements ArgKey {
		NOTIFY_ME
	}
}
