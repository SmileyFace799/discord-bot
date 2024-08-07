package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Shows the music player in a new message.
 */
public class ShowPlayerAction extends BotAction<BotAction.ArgKey> {

	/**
	 * Makes the show player action.
	 */
	public ShowPlayerAction() {
		super(
				new ActionCommand<>(Commands
						.slash("showplayer", "Shows the audio player")
						.setGuildOnly(true)
				),
				new InVoiceWithBot()
		);
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		MusicManager
				.getInstance()
				.showPlayerMessage(Objects.requireNonNull(event.getGuild()));
		event.reply("Player shown!").setEphemeral(true).queue();
	}
}
