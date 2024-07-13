package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Makes the bot leave the current voice channel, & stops any music currently playing.
 */
public class LeaveAction extends BotAction<BotAction.ArgKey> {
	/**
	 * Makes the leave action.
	 */
	public LeaveAction() {
		super(
				new ActionCommand<>(Commands
						.slash("leave", "Makes the bot leave the voice channel you're in")
						.setGuildOnly(true),
						"stop"
				),
				new ActionButton<>(ButtonStyle.DANGER, "stopButton", "Stop"),
				new InVoiceWithBot()
		);
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		MusicManager.getInstance().stop(Objects.requireNonNull(event.getMember()));
		event.reply("Left channel!").setEphemeral(true).queue();
	}
}
