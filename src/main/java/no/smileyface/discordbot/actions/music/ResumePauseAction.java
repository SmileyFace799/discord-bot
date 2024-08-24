package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.buttons.ResumePauseButton;
import no.smileyface.discordbot.actions.music.commands.ResumePauseCommand;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Toggles the resume/pause state of the music player.
 */
public class ResumePauseAction extends BotAction<GenericBotAction.ArgKey> {
	/**
	 * Creates the resume pause action.
	 */
	public ResumePauseAction(ActionManager manager) {
		super(manager, Set.of(
				new ResumePauseCommand.Resume(),
				new ResumePauseCommand.Pause()
		), new InVoiceWithBot());
		addButtons(new ResumePauseButton());
	}

	@Override
	protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
		boolean paused;
		Boolean value = args.getValue(Boolean.class);
		if (value == null) {
			paused = MusicManager
					.getInstance()
					.togglePaused(Objects.requireNonNull(event.getMember()));
		} else {
			paused = value;
			MusicManager.getInstance().setPaused(paused, Objects.requireNonNull(event.getMember()));
		}
		event.reply(paused ? "Music paused!" : "Music resumed!").setEphemeral(true).queue();
	}
}
