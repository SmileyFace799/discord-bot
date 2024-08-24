package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.buttons.ShuffleButton;
import no.smileyface.discordbot.actions.music.commands.ShuffleCommand;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Toggles shuffle.
 */
public class ShuffleAction extends BotAction<GenericBotAction.ArgKey> {
	/**
	 * Makes the shuffle action.
	 */
	public ShuffleAction(ActionManager manager) {
		super(manager, new ShuffleCommand(), new InVoiceWithBot());
		addButtons(new ShuffleButton());
	}

	@Override
	protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
		event.reply(String.format("Shuffle %s!",
				MusicManager.getInstance()
						.toggleShuffled(Objects.requireNonNull(event.getMember()))
						? "enabled" : "disabled"
		)).setEphemeral(true).queue();
	}
}
