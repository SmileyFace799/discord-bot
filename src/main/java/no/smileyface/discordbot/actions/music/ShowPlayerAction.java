package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.commands.ShowPlayerCommand;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Shows the music player in a new message.
 */
public class ShowPlayerAction extends BotAction<GenericBotAction.ArgKey> {

	/**
	 * Makes the show player action.
	 */
	public ShowPlayerAction(ActionManager manager) {
		super(manager, new ShowPlayerCommand(), new InVoiceWithBot());
	}

	@Override
	protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
		MusicManager
				.getInstance()
				.showPlayerMessage(Objects.requireNonNull(event.getGuild()));
		event.reply("Player shown!").setEphemeral(true).queue();
	}
}
