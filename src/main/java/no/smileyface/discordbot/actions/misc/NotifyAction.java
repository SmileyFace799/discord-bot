package no.smileyface.discordbot.actions.misc;

import java.io.IOException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.misc.commands.NotifyCommand;
import no.smileyface.discordbot.files.AnnouncementWhitelist;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.files.LongCollectionFileInterface;

/**
 * Allows the user to be notified about updates & expected downtime.
 */
public class NotifyAction extends BotAction<NotifyAction.Key> {
	public NotifyAction(ActionManager manager) {
		super(manager, new NotifyCommand());
	}

	@Override
	protected void execute(IReplyCallback event, Node<Key, Object> args) {
		String response;
		long id = event.getUser().getIdLong();
		try {
			LongCollectionFileInterface announcementWhitelist = AnnouncementWhitelist.getInstance();
			boolean notifyOld = announcementWhitelist.contains(id);
			if (args.hasChild(Key.NOTIFY_ME)) {
				boolean notifyNew = args.getValue(Key.NOTIFY_ME, Boolean.class);
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
	public enum Key implements ArgKey {
		NOTIFY_ME
	}
}
