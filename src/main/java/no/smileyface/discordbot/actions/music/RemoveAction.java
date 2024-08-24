package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.commands.RemoveCommand;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Removes one or more songs from the queue.
 */
public class RemoveAction extends BotAction<RemoveAction.Key> {
	public RemoveAction(ActionManager manager) {
		super(manager, new RemoveCommand(), new InVoiceWithBot());
	}

	@Override
	protected void execute(IReplyCallback event, Node<Key, Object> args) {
		int index = args.getValue(Key.INDEX, Integer.class);
		int endIndex = args.hasChild(Key.END_INDEX)
				? args.getValue(Key.END_INDEX, Integer.class)
				: index;
		if (index > endIndex) {
			event.reply("End index cannot be greater than start index")
					.setEphemeral(true)
					.queue();
		} else {
			MusicManager.getInstance().remove(
					index,
					endIndex,
					Objects.requireNonNull(event.getMember()),
					(clampedStart, clampedEnd) -> event.reply(
							Objects.equals(clampedStart, clampedEnd)
									? String.format("Removed track %s!", clampedStart)
									: String.format("Removed all tracks from %s to %s!",
									clampedStart, clampedEnd)
					).setEphemeral(true).queue()
			);
		}
	}

	/**
	 * Keys for args map.
	 */
	public enum Key implements ArgKey {
		INDEX,
		END_INDEX
	}
}
