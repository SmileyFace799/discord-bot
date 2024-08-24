package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.GenericBotAction;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Superclass for {@link Resume} and {@link Pause}.
 */
public sealed class ResumePauseCommand extends ActionCommand<GenericBotAction.ArgKey> {
	private final boolean pause;

	/**
	 * Creates the command.
	 *
	 * @param pause If the command should pause the player, will resume otherwise.
	 */
	private ResumePauseCommand(boolean pause) {
		super(Commands.slash(
				pause ? "pause" : "resume",
				pause ? "Pauses the music" : "Resumes music when paused"
		).setGuildOnly(true));
		this.pause = pause;
	}

	@Override
	public Node<GenericBotAction.ArgKey, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		return new Node<>(pause);
	}

	/**
	 * Command for triggering
	 * {@link no.smileyface.discordbot.actions.music.ResumePauseAction ResumePauseAction}.
	 */
	public static non-sealed class Resume extends ResumePauseCommand {
		/**
		 * Creates the command.
		 */
		public Resume() {
			super(false);
		}
	}

	/**
	 * Command for triggering
	 * {@link no.smileyface.discordbot.actions.music.ResumePauseAction ResumePauseAction}.
	 */
	public static non-sealed class Pause extends ResumePauseCommand {
		/**
		 * Creates the command.
		 */
		public Pause() {
			super(true);
		}
	}
}
