package org.smileyface.commands.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

public class LeaveCommand extends BotCommand {
    public LeaveCommand() {
        super(Commands
                        .slash("leave", "Makes the bot leave the voice channel you're in")
                        .setGuildOnly(true),
                Set.of("stop"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Member author = Objects.requireNonNull(event.getMember());
        AudioChannel audioChannel = Checks.authorInVoice(author);
        Checks.botConnectedToAuthorVoice(audioChannel);

        TrackQueue queue = MusicManager.getInstance().getQueue(author.getGuild().getIdLong());
        Music.leaveVoiceIfConnected(author.getGuild());
        queue.getTrackQueueEmbed().setLastCommand(author, "Stopped the music, "
                        + "and made the bot leave voice channel");
        event.reply("Left channel!").setEphemeral(true).queue();
    }
}
