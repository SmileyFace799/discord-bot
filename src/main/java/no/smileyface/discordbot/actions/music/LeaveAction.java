package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.buttons.StopButton;
import no.smileyface.discordbot.actions.music.commands.LeaveCommand;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Makes the bot leave the current voice channel, & stops any music currently playing.
 */
public class LeaveAction extends BotAction<GenericBotAction.ArgKey> {
	/**
	 * Makes the leave action.
	 */
	public LeaveAction(ActionManager manager) {
		super(manager, new LeaveCommand(), new InVoiceWithBot());
		addButtons(new StopButton());
	}

	@Override
	protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
		MusicManager.getInstance().stop(Objects.requireNonNull(event.getMember()));
		event.reply("Left channel!").setEphemeral(true).queue();
	}
}
