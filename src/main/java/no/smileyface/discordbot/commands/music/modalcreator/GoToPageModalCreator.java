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
 * Creates a modal for going to a specific page of the queue.
 */
public class GoToPageModalCreator extends BotAction<BotAction.ArgKey> {
	/**
	 * Creates the go to page modal creator.
	 */
	public GoToPageModalCreator() {
		super(new ActionButton<>(
				ButtonStyle.SECONDARY,
				"goToPageButton",
				"Go To Page"
		), new InVoiceWithBot());
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		ActionUtil.tryReplyModal(event, inputs.getModals(), "goToPageModal");
	}
}
