package no.smileyface.discordbot.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.audio.MusicManager;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Toggles shuffle.
 */
public class ShuffleAction extends BotAction<BotAction.ArgKey> {
	/**
	 * Makes the shuffle action.
	 */
	public ShuffleAction() {
		super(
				new ActionCommand<>(Commands
						.slash("shuffle", "Toggles shuffle")
						.setGuildOnly(true),
						"shf"
				),
				new ActionButton<>(ButtonStyle.PRIMARY, "shuffleButton", "Shuffle"),
				new InVoiceWithBot()
		);
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		event.reply(String.format("Shuffle %s!",
				MusicManager.getInstance()
						.getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
						.toggleShuffle()
						? "enabled" : "disabled"
		)).setEphemeral(true).queue();
	}
}
