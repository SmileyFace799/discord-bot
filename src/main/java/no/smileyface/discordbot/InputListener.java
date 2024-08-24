package no.smileyface.discordbot;

import java.io.IOException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import no.smileyface.discordbot.files.AnnouncementWhitelist;
import no.smileyface.discordbotframework.ActionInitializer;
import no.smileyface.discordbotframework.ActionManager;

/**
 * Input listener for the music bot.
 */
public class InputListener extends ActionManager {
	private User cachedBotOwner;

	public InputListener(ActionInitializer actionInitializer) {
		super(actionInitializer);
		this.cachedBotOwner = null;
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
						.reply("Message has been announced!")
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
