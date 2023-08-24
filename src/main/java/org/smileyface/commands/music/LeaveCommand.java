package org.smileyface.commands.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Makes the bot leave the current voice channel, & stops any music currently playing.
 */
public class LeaveCommand extends BotCommand {
    /**
     * Makes the leave command.
     */
    public LeaveCommand() {
        super(Commands
                        .slash("leave", "Makes the bot leave the voice channel you're in")
                        .setGuildOnly(true),
                Set.of("stop"));
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
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.botConnectedToAuthorVoice(event);
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        Member author = Objects.requireNonNull(event.getMember());
        Guild guild = author.getGuild();

        TrackQueue leftQueue = leaveSilently(guild);
        if (leftQueue != null) {
            leftQueue.getTrackQueueEmbed().setLastCommand(author, "Stopped the music, "
                    + "and made the bot leave voice channel");
        }
        event.reply("Left channel!").setEphemeral(true).queue();
    }
}
