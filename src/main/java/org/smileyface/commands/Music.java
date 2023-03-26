package org.smileyface.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.audio.LavaPlayerJdaWrapper;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.Track;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.CheckFailedException;
import org.smileyface.checks.Checks;

/**
 * Contains all music-related commands.
 */
public class Music {
    private static final Map<String, BotCommand> COMMANDS = Stream.of(
            new BotCommand(Commands
                    .slash("join", "Joins a voice channel")
                    .setGuildOnly(true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    joinVoiceOfMember(Objects.requireNonNull(event.getMember()),
                            event.getGuildChannel());
                    event.reply("Joined channel!").setEphemeral(true).queue();
                }
            },

            new BotCommand(Commands
                    .slash("leave", "Makes the bot leave the voice channel you're in")
                    .setGuildOnly(true),
                    Set.of("stop")
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    AudioChannel audioChannel = Checks.authorInVoice(
                            Objects.requireNonNull(event.getMember()));
                    Checks.botConnectedToAuthorVoice(audioChannel);

                    leaveVoiceIfConnected(Objects.requireNonNull(event.getGuild()));
                    event.reply("Left channel!").setEphemeral(true).queue();
                }
            },

            new BotCommand(Commands
                    .slash("play", "Plays a song")
                    .setGuildOnly(true)
                    .addOption(OptionType.STRING, "input",
                            "A search or URL for the song to play. "
                                    + "Search is done through YouTube", true)
                    .addOption(OptionType.BOOLEAN, "songsearch",
                            "If the YouTube search should search for songs only. "
                                    + "Ignored if input is a URL  (Default: False)"),
                    Set.of("p")
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    Member author = Objects.requireNonNull(event.getMember());
                    joinIfNotConnected(author, event.getGuildChannel());

                    String identifier = Objects.requireNonNull(
                            event.getOption("input")).getAsString();

                    if ((identifier.startsWith("https://") || identifier.startsWith("http://"))) {
                        event.deferReply().queue();
                        MusicManager.getInstance().queue(identifier, author, event.getHook());
                    } else {
                        throw new CheckFailedException("The input is not a link. "
                                + "YouTube search feature is not implemented yet");
                    }
                }
            },

            new BotCommand(Commands
                    .slash("skip", "Skips the current song")
                    .setGuildOnly(true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    Checks.botConnectedToAuthorVoice(Checks.authorInVoice(
                            Objects.requireNonNull(event.getMember())));

                    Checks.isPlaying(Objects.requireNonNull(event.getGuild()).getId()).skip();
                    event.reply("Song skipped!").queue();
                }
            },

            new BotCommand(Commands
                    .slash("queue", "Views the current song queue")
                    .setGuildOnly(true)
                    .addOption(OptionType.INTEGER, "page", "The queue page to view. "
                            + "If invalid, the closest availible page is shown  (Default: 1)"),
                    Set.of("q")
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    int tracksPerPage = 10;

                    TrackQueue queue = Checks.isPlaying(
                            Objects.requireNonNull(event.getGuild()).getId());

                    OptionMapping pageOption = event.getOption("page");
                    List<Track> tracks = new ArrayList<>();
                    tracks.add(queue.getCurrentlyPlaying());
                    tracks.addAll(queue.getTracks());
                    int lastPage = Math.floorDiv(tracks.size(), tracksPerPage) + 1;
                    int page = pageOption != null
                            ? Math.max(1, Math.min(lastPage, pageOption.getAsInt()))
                            : 1;
                    event.reply("**Current queue (Page " + page + " of " + lastPage + "):**\n"
                            + String.join("\n", tracks
                            .subList((page - 1) * tracksPerPage,
                                    Math.min(page * tracksPerPage, tracks.size()))
                            .stream()
                            .map(track -> "  **" + (track.equals(queue.getCurrentlyPlaying())
                                    ? "Playing" : (tracks.indexOf(track) + 1)) + ":** "
                                    + track.getTitle())
                            .toList())
                    ).setEphemeral(true).queue();
                }
            }
    ).collect(Collectors.toMap(
                    command -> command.getData().getName(),
                    command -> command
            )
    );

    private Music() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, BotCommand> getCommands() {
        return COMMANDS;
    }

    private static void joinVoiceOfMember(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CheckFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        Guild memberGuild = memberToJoin.getGuild();
        AudioManager audioManager = memberGuild.getAudioManager();
        Checks.botNotConnected(audioManager);

        AudioPlayer player = MusicManager.getInstance().createPlayer(playerChannel);
        audioManager.setSendingHandler(new LavaPlayerJdaWrapper(player));
        audioManager.openAudioConnection(audioChannel);
    }

    private static void joinIfNotConnected(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CheckFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        try {
            joinVoiceOfMember(memberToJoin, playerChannel);
        } catch (CheckFailedException cfe) {
            Checks.botConnectedToAuthorVoice(audioChannel);
            audioChannel.getGuild().getAudioManager();
        }
    }

    /**
     * Leaves voice if the bot is in one.
     *
     * @param guild The guild where the bot should leave voice.
     */
    public static void leaveVoiceIfConnected(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            guild.getAudioManager().closeAudioConnection();
        }
        try {
            Checks.isPlaying(guild.getId());
            MusicManager.getInstance().stop(guild.getId());
        } catch (CheckFailedException cfe) {
            //Do nothing.
        }
    }
}
