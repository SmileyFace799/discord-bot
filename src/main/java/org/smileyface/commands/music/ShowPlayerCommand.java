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
 * Shows the music player in a new message.
 */
public class ShowPlayerCommand extends BotCommand {

    /**
     * Makes the show player command.
     */
    public ShowPlayerCommand() {
        super(Commands
                .slash("showplayer", "Shows the audio player")
                .setGuildOnly(true)
        );
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.botConnectedToAuthorVoice(event);
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        MusicManager
                .getInstance()
                .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getTrackQueueMessage()
                .showPlayer();
        event.reply("Player shown!").setEphemeral(true).queue();
    }
}
