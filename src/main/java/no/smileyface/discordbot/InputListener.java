package no.smileyface.discordbot;

import java.io.IOException;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import no.smileyface.discordbot.files.AnnouncementWhitelist;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Input listener for the music bot.
 */
public class InputListener extends ActionManager {
	private User cachedBotOwner = null;

	/**
	 * Constructor.
	 *
	 * @param actions The collection of actions that the bot can perform.
	 */
	public InputListener(Collection<? extends BotAction<? extends BotAction.ArgKey>> actions) {
		super(actions);
	}

	private void announce(Message message, MessageReceivedEvent originalEvent) {
		if (message == null) {
			originalEvent
					.getMessage()
					.reply("Could not get the content of the announcement message")
					.queue();
		} else {
			JDA jda = message.getJDA();
			try {
				AnnouncementWhitelist.getInstance().forEach(id ->
						jda.retrieveUserById(id).queue(user ->
								user.openPrivateChannel().queue(channel -> channel
										.sendMessage(message.getContentRaw())
										.queue()
								)
						)
				);
				message
						.reply("Announcing this to the owner of every guild the bot is in")
						.queue();
			} catch (IOException ioe) {
				message.reply("IOException occurred: " + ioe.getMessage()).queue();
			}
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		MessageReference announcement = event.getMessage().getMessageReference();
		if (event.getMessage().getContentRaw().equals("--newBotAnnouncement")
				&& announcement != null && !event.getAuthor().isBot()
		) {
			if (cachedBotOwner == null) {
				cachedBotOwner = event.getJDA().retrieveApplicationInfo().complete().getOwner();
			}
			if (cachedBotOwner.equals(event.getAuthor())) {
				announce(announcement.getMessage(), event);
			}
		}
	}
}
