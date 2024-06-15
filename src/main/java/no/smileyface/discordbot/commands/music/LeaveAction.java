package no.smileyface.discordbot.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
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

	/**
	 * Leaves voice of a guild, if possible.
	 *
	 * @param guild The guild to leave voice of
	 * @return The queue associated with the voice connection that was left.
	 *         Will be {@code null} if the bot didn't actually leave voice
	 */
	public static TrackQueue leaveSilently(Guild guild) {
		guild.getAudioManager().closeAudioConnection();
		TrackQueue queue = MusicManager.getInstance().getQueue(guild.getIdLong());
		if (queue != null) {
			MusicManager.getInstance().stop(guild.getIdLong());
		}
		return queue;
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		Member author = Objects.requireNonNull(event.getMember());
		Guild guild = author.getGuild();

		TrackQueue leftQueue = leaveSilently(guild);
		if (leftQueue != null) {
			leftQueue.getTrackQueueMessage().setLastCommand(author, "Stopped the music, "
					+ "and made the bot leave voice channel");
		}
		event.reply("Left channel!").setEphemeral(true).queue();
	}
}
