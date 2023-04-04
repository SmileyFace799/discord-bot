package org.smileyface.commands.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicTrack;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

public class QueueCommand extends BotCommand {
    public QueueCommand() {
        super(Commands
                        .slash("queue", "Views the current song queue")
                        .setGuildOnly(true)
                        .addOption(OptionType.INTEGER, "page",
                                "The queue page to view. "
                                + "If invalid, the closest available page is shown  (Default: 1)"),
                Set.of("q"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        int tracksPerPage = 10;

        TrackQueue queue = Checks.isPlaying(
                Objects.requireNonNull(event.getGuild()).getIdLong());

        OptionMapping pageOption = event.getOption("page");
        List<MusicTrack> musicTracks = new ArrayList<>();
        musicTracks.add(queue.getCurrentlyPlaying());
        musicTracks.addAll(queue.getTracks());
        int lastPage = Math.floorDiv(musicTracks.size() - 1, tracksPerPage) + 1;
        int page = pageOption != null
                ? Math.max(1, Math.min(lastPage, pageOption.getAsInt()))
                : 1;
        event.reply("**Current queue (Page " + page + " of " + lastPage + "):**\n"
                + String.join("\n", musicTracks
                .subList((page - 1) * tracksPerPage,
                        Math.min(page * tracksPerPage, musicTracks.size()))
                .stream()
                .map(musicTrack -> "  **"
                        + (musicTrack.equals(queue.getCurrentlyPlaying())
                        ? "Playing" : (musicTracks.indexOf(musicTrack) + 1)) + ":** "
                        + musicTrack.getTitle())
                .toList())
        ).setEphemeral(true).queue();
    }
}
