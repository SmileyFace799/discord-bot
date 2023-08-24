package org.smileyface.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.audio.LavaPlayerJdaWrapper;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Makes the bot join the user's current voice channel.
 */
public class JoinCommand extends BotCommand {

    /**
     * Makes the join command.
     */
    public JoinCommand() {
        super(Commands
                .slash("join", "Joins a voice channel")
                .setGuildOnly(true)
        );
    }

    /**
     * Join the author's voice channel, if possible.
     *
     * @return The track queue associated with the newly made voice connection.
     *         This will be {@code null} if the bot didn't actually join voice
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
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.authorInVoice(event);
        Checks.botNotConnected(event);
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
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
