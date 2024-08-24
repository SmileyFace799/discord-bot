package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.buttons.SkipButton;
import no.smileyface.discordbot.actions.music.commands.SkipCommand;
import no.smileyface.discordbot.checks.BotIsPlaying;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Skips the current song.
 */
public class SkipAction extends BotAction<SkipAction.Key> {
	/**
	 * Makes the skip action.
	 */
	public SkipAction(ActionManager manager) {
		super(manager, new SkipCommand(), new InVoiceWithBot(), new BotIsPlaying());
		addButtons(new SkipButton());
	}

	@Override
	protected void execute(
			IReplyCallback event,
			Node<Key, Object> args
	) {
		int actualAmount = MusicManager.getInstance().skip(
				Objects.requireNonNull(event.getMember()),
				args.getValue(Key.AMOUNT, Integer.class)
		);
		event.reply(String.format("Skipped %s song%s",
				actualAmount,
				actualAmount == 1 ? "" : "s")
		).setEphemeral(true).queue();
	}

	/**
	 * Keys for args map.
	 */
	public enum Key implements ArgKey {
		AMOUNT
	}
}
