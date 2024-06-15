package no.smileyface.discordbot.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.audio.MusicManager;
import no.smileyface.discordbot.audio.TrackQueue;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Changes the player's repeat mode.
 */
public class RepeatAction extends BotAction<RepeatAction.RepeatKey> {
	private static class RepeatCommand extends ActionCommand<RepeatKey> {
		public RepeatCommand() {
			super(
					Commands
							.slash("repeat", "Changes the repeat mode")
							.addOption(OptionType.STRING, RepeatKey.REPEAT_MODE.str(),
									"The repeat mode to set. Must be \"song\", "
											+ "\"queue\" or \"off", true)
							.setGuildOnly(true),
					"rpt"
			);
		}

		@Override
		public MultiTypeMap<RepeatKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<RepeatKey> args = new MultiTypeMap<>();
			args.put(RepeatKey.REPEAT_MODE, event.getOption(RepeatKey.REPEAT_MODE.str(),
					repeatStr -> {
						TrackQueue.Repeat repeat;
						try {
							repeat = TrackQueue.Repeat.getRepeat(repeatStr.getAsString()
									.replace("\"", ""));
						} catch (IllegalArgumentException iae) {
							repeat = null;
						}
						return repeat;
					}
			));
			return args;
		}
	}

	private static class RepeatButton extends ActionButton<RepeatKey> {
		public RepeatButton() {
			super(ButtonStyle.PRIMARY, "repeatButton", "Repeat Song / Queue / Off");
		}

		@Override
		public MultiTypeMap<RepeatKey> createArgs(ButtonInteractionEvent event) {
			TrackQueue.Repeat oldRepeatMode = MusicManager
					.getInstance()
					.getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
					.getRepeat();

			MultiTypeMap<RepeatKey> args = new MultiTypeMap<>();
			args.put(RepeatKey.REPEAT_MODE, switch (oldRepeatMode) {
				case NO_REPEAT -> TrackQueue.Repeat.REPEAT_SONG;
				case REPEAT_SONG -> TrackQueue.Repeat.REPEAT_QUEUE;
				case REPEAT_QUEUE -> TrackQueue.Repeat.NO_REPEAT;
			});
			return args;
		}
	}

	/**
	 * Makes the repeat action.
	 */
	public RepeatAction() {
		super(new RepeatCommand(), new RepeatButton(), new InVoiceWithBot());
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<RepeatKey> args,
			InputRecord inputs
	) {
		TrackQueue.Repeat repeat = args.get(RepeatKey.REPEAT_MODE, TrackQueue.Repeat.class);
		if (repeat != null) {
			MusicManager.getInstance()
					.getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
					.setRepeat(repeat);
			event.reply("Set repeat mode to: " + repeat.getStr()).setEphemeral(true).queue();
		} else {
			event.reply("Unknown repeat mode. "
							+ "The valid modes are \"song\", \"queue\" or \"off\""
					).setEphemeral(true)
					.queue();
		}
	}

	/**
	 * Keys for args map.
	 */
	public enum RepeatKey implements ArgKey {
		REPEAT_MODE,
	}
}
