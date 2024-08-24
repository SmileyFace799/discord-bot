package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.buttons.RepeatButton;
import no.smileyface.discordbot.actions.music.commands.RepeatCommand;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.TrackQueue;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Changes the player's repeat mode.
 */
public class RepeatAction extends BotAction<RepeatAction.Key> {
	/**
	 * Makes the repeat action.
	 */
	public RepeatAction(ActionManager manager) {
		super(manager, new RepeatCommand(), new InVoiceWithBot());
		addButtons(new RepeatButton());
	}

	@Override
	protected void execute(IReplyCallback event, Node<Key, Object> args) {
		TrackQueue.Repeat repeat;
		if (args.hasChild(Key.CHANGE_NEXT)) {
			repeat = MusicManager
					.getInstance()
					.changeRepeat(Objects.requireNonNull(event.getMember()));
		} else {
			repeat = args.getValue(Key.REPEAT_MODE, TrackQueue.Repeat.class);
			if (repeat == null) {
				event.reply("Unknown repeat mode. "
						+ "The valid modes are \"song\", \"queue\" or \"off\""
				).setEphemeral(true).queue();
			}
		}
		if (repeat != null) {
			MusicManager
					.getInstance()
					.setRepeat(repeat, Objects.requireNonNull(event.getMember()));
			event.reply("Set repeat mode to: " + repeat.getStr()).setEphemeral(true).queue();
		}
	}

	/**
	 * Keys for args map.
	 */
	public enum Key implements ArgKey {
		REPEAT_MODE,
		CHANGE_NEXT
	}
}
