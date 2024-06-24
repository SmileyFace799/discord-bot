package no.smileyface.discordbot.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Removes one or more songs from the queue.
 */
public class RemoveAction extends BotAction<RemoveAction.RemoveKey> {
	private static class RemoveCommand extends ActionCommand<RemoveKey> {

		/**
		 * Constructor.
		 */
		public RemoveCommand() {
			super(Commands
					.slash("remove", "Removes one or more songs from the queue")
					.setGuildOnly(true)
					.addOption(
							OptionType.INTEGER,
							RemoveKey.INDEX.str(),
							"Index of the song to remove",
							true
					).addOption(
							OptionType.INTEGER,
							RemoveKey.END_INDEX.str(),
							"Makes this remove multiple songs, from \"index\" to \"end_index\""
					)
			);
		}

		@Override
		public MultiTypeMap<RemoveKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<RemoveKey> args = new MultiTypeMap<>();
			args.put(
					RemoveKey.INDEX,
					Objects.requireNonNull(event.getOption(RemoveKey.INDEX.str())).getAsInt()
			);
			OptionMapping endIndex = event.getOption(RemoveKey.END_INDEX.str());
			if (endIndex != null) {
				args.put(RemoveKey.END_INDEX, endIndex.getAsInt());
			}
			return args;
		}
	}

	public RemoveAction() {
		super(new RemoveCommand(), new InVoiceWithBot());
	}

	@Override
	protected void execute(IReplyCallback event, MultiTypeMap<RemoveKey> args, InputRecord inputs) {
		int index = args.get(RemoveKey.INDEX, Integer.class);
		int endIndex = args.containsKey(RemoveKey.END_INDEX)
				? args.get(RemoveKey.END_INDEX, Integer.class)
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
	public enum RemoveKey implements ArgKey {
		INDEX,
		END_INDEX
	}
}
