package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Pauses the music.
 */
public class PauseCommand extends BotCommand {
    /**
     * Makes the pause command.
     */
    public PauseCommand() {
        super(Commands
                .slash("pause", "Pauses the music")
                .setGuildOnly(true));
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.botConnectedToAuthorVoice(event);
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        MusicManager.getInstance()
                .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getPlayer()
                .setPaused(true);
        event.reply("Music paused!").setEphemeral(true).queue();
    }
}
