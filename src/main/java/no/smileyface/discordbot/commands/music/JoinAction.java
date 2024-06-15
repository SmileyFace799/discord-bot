package no.smileyface.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import no.smileyface.discordbot.audio.LavaPlayerJdaWrapper;
import no.smileyface.discordbot.audio.MusicManager;
import no.smileyface.discordbot.audio.TrackQueue;
import no.smileyface.discordbot.checks.BotNotInVoice;
import no.smileyface.discordbot.checks.InVoice;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Makes the bot join the user's current voice channel.
 */
public class JoinAction extends BotAction<BotAction.ArgKey> {

	/**
	 * Makes the join action.
	 */
	public JoinAction() {
		super(
				new ActionCommand<>(Commands
						.slash("join", "Joins a voice channel")
						.setGuildOnly(true)
				),
				new InVoice(), new BotNotInVoice()
		);
	}

	/**
	 * Join the author's voice channel, if possible.
	 *
	 * @return The track queue associated with the newly made voice connection.
	 * This will be {@code null} if the bot didn't actually join voice
	 */
	protected static TrackQueue joinSilently(
			AudioChannel audioChannel, GuildMessageChannel playerChannel
	) {
		AudioManager audioManager = audioChannel.getGuild().getAudioManager();
		TrackQueue queue = null;
		if (!audioManager.isConnected()) {
			AudioPlayer player = MusicManager.getInstance().createPlayer(playerChannel);
			audioManager.setSendingHandler(new LavaPlayerJdaWrapper(player));
			audioManager.openAudioConnection(audioChannel);
			queue = MusicManager.getInstance().getQueue(player);
		}
		return queue;
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		Member member = Objects.requireNonNull(event.getMember());
		TrackQueue queue = joinSilently(
				Objects.requireNonNull(
						Objects.requireNonNull(member.getVoiceState()).getChannel()
				),
				(GuildMessageChannel) event.getMessageChannel()
		);
		if (queue != null) {
			queue.getTrackQueueMessage()
					.setLastCommand(member, "Joined the voice channel");
		}
		event.reply("Joined channel!").setEphemeral(true).queue();
	}
}
