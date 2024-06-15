package no.smileyface.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
 * Toggles the resume/pause state of the music player.
 */
public class ResumePauseAction extends BotAction<ResumePauseAction.ResumePauseKey> {
	private static class ResumeCommand extends ActionCommand<ResumePauseKey> {
		public ResumeCommand() {
			super(Commands
					.slash("resume", "Resumes music when paused")
					.setGuildOnly(true)
			);
		}

		@Override
		public MultiTypeMap<ResumePauseKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<ResumePauseKey> args = new MultiTypeMap<>();
			args.put(ResumePauseKey.SET_PAUSED, false);
			return args;
		}
	}

	private static class PasueCommand extends ActionCommand<ResumePauseKey> {
		public PasueCommand() {
			super(Commands
					.slash("pause", "Pauses the music")
					.setGuildOnly(true)
			);
		}

		@Override
		public MultiTypeMap<ResumePauseKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<ResumePauseKey> args = new MultiTypeMap<>();
			args.put(ResumePauseKey.SET_PAUSED, true);
			return args;
		}
	}

	private static class ResumePauseButton extends ActionButton<ResumePauseKey> {
		public ResumePauseButton() {
			super(
					ButtonStyle.PRIMARY,
					"playPauseButton",
					"Play / Pause",
					Emoji.fromUnicode("⏯")
			);
		}
	}

	/**
	 * Creates the resume pause action.
	 */
	public ResumePauseAction() {
		super(
				Set.of(new ResumeCommand(), new PasueCommand()),
				Set.of(new ResumePauseButton()),
				null,
				new InVoiceWithBot()
		);
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ResumePauseKey> args,
			InputRecord inputs
	) {
		AudioPlayer player = MusicManager
				.getInstance()
				.getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
				.getPlayer();
		boolean paused = args.containsKey(ResumePauseKey.SET_PAUSED)
				? args.get(ResumePauseKey.SET_PAUSED, Boolean.class)
				: !player.isPaused();
		player.setPaused(paused);
		event.reply(paused ? "Music paused!" : "Music resumed!").setEphemeral(true).queue();
	}

	/**
	 * Keys for args map.
	 */
	public enum ResumePauseKey implements ArgKey {
		SET_PAUSED
	}
}
