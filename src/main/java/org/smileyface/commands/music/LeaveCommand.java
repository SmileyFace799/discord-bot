package org.smileyface.commands.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

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

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Member author = Objects.requireNonNull(event.getMember());
        Checks.botConnectedToMemberVoice(author);

        TrackQueue queue = MusicManager.getInstance().getQueue(author.getGuild().getIdLong());
        Music.leaveVoiceIfConnected(author.getGuild());
        queue.getTrackQueueEmbed().setLastCommand(author, "Stopped the music, "
                        + "and made the bot leave voice channel");
        event.reply("Left channel!").setEphemeral(true).queue();
    }
}
