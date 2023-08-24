package org.smileyface.commands.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.MusicTrack;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Shows the current song queue.
 */
public class QueueCommand extends BotCommand {
    /**
     * Makes the queue command.
     */
    public QueueCommand() {
        super(
                Commands.slash("queue", "Views the current song queue")
                        .setGuildOnly(true)
                        .addOption(
                                OptionType.INTEGER,
                                ArgKeys.PAGE,
                                "The queue page to view. "
                                        + "If invalid, the closest available page is shown"
                                        + "  (Default: 1)"
                        ),
                Set.of("q"));
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.isPlaying(event);
    }

    @Override
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(ArgKeys.PAGE, event.getOption(ArgKeys.PAGE, 1, OptionMapping::getAsInt));
        return args;
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        int tracksPerPage = 10;

        TrackQueue queue = MusicManager.getInstance().getQueue(
                Objects.requireNonNull(event.getGuild()).getIdLong()
        );

        List<MusicTrack> musicTracks = new ArrayList<>();
        musicTracks.add(queue.getCurrentlyPlaying());
        musicTracks.addAll(queue.getTracks());
        int lastPage = Math.floorDiv(musicTracks.size() - 1, tracksPerPage) + 1;
        int page = Math.max(1, Math.min(lastPage, args.get(ArgKeys.PAGE, Integer.class)));
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

    /**
     * Keys for args map.
     */
    public static class ArgKeys {
        public static final String PAGE = "page";

        private ArgKeys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
