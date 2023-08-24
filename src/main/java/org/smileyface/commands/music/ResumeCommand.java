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
 * Resumes music when music is paused.
 */
public class ResumeCommand extends BotCommand {
    /**
     * Makes the resume command.
     */
    public ResumeCommand() {
        super(Commands
                .slash("resume", "Resumes music when paused")
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
                .setPaused(false);
        event.reply("Music resumed!").setEphemeral(true).queue();
    }
}
