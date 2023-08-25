package org.smileyface.commands.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Toggles shuffle.
 */
public class ShuffleCommand extends BotCommand {
    /**
     * Makes the command.
     */
    public ShuffleCommand() {
        super(
                Commands
                        .slash("shuffle", "Toggles shuffle")
                        .setGuildOnly(true),
                Set.of("shf"));
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.botConnectedToAuthorVoice(event);
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        event.reply(String.format("Shuffle %s!",
                MusicManager.getInstance()
                        .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                        .toggleShuffle()
                ? "enabled" : "disabled"
        )).setEphemeral(true).queue();
    }
}
