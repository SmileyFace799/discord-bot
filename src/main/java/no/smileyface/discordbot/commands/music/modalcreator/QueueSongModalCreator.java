package no.smileyface.discordbot.commands.music.modalcreator;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.commands.ActionUtil;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Creates a modal for queuing a song.
 */
public class QueueSongModalCreator extends BotAction<BotAction.ArgKey> {
	/**
	 * Makes the queue song modal creator.
	 */
	public QueueSongModalCreator() {
		super(new ActionButton<>(
				ButtonStyle.SECONDARY,
				"queueButton",
				"Queue more"
		), new InVoiceWithBot());
	}

	@Override
	protected void execute(IReplyCallback event, MultiTypeMap<ArgKey> args, InputRecord inputs) {
		ActionUtil.tryReplyModal(event, inputs.getModals(), "queueSongModal");
	}
}
