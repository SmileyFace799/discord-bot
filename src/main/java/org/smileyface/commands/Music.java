package org.smileyface.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.hc.core5.http.ParseException;
import org.smileyface.audio.LavaPlayerJdaWrapper;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.MusicTrack;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * Contains all music-related commands.
 */
public class Music {
    private static final String YOUTUBE_SEARCH = "ytsearch:";
    private static final String YOUTUBE_SONG_FILTER = " \"auto-generated by YouTube\"";
    private static final Map<String, BotCommand> COMMANDS = Stream.of(
            new BotCommand(Commands
                    .slash("join", "Joins a voice channel")
                    .setGuildOnly(true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
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
                public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
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
                public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
                    Member author = Objects.requireNonNull(event.getMember());
                    joinIfNotConnected(author, event.getGuildChannel());

                    List<String> identifiers = new ArrayList<>();
                    String identifier = Objects.requireNonNull(
                            event.getOption("input")).getAsString();

                    if ((identifier.startsWith("https://") || identifier.startsWith("http://"))) {
                        if (
                                identifier.toLowerCase().contains("youtube.com")
                                        && identifier.contains("v=")
                                        && identifier.contains("list=")
                        ) {
                            //If it's a YouTube video in a playlist, only play that video
                            identifier = "https://www.youtube.com/watch?v="
                                    + identifier.split("v=")[1].split("&")[0];
                        } else if (SpotifyManager.getInstance().getApi() != null) {
                            try {
                                SpotifyApi api = SpotifyManager.getInstance().getApi();
                                if (identifier.contains("open.spotify.com/track/")) {
                                    identifier = getYouTubeSearch(api
                                            .getTrack(identifier.split("track/")[1].split("\\?")[0])
                                            .build()
                                            .execute());
                                }
                            } catch (IOException | SpotifyWebApiException | ParseException e) {
                                throw new CommandFailedException("Spotify error: "
                                        + e.getMessage());
                            }
                        } else if (identifier.contains("open.spotify.com")) {
                            throw new CommandFailedException("Spotify is not supported");
                        }
                    } else {
                        identifier = YOUTUBE_SEARCH + identifier;
                        OptionMapping songSearchOption = event.getOption("songsearch");
                        if (songSearchOption != null && songSearchOption.getAsBoolean()) {
                            identifier += YOUTUBE_SONG_FILTER;
                        }
                    }
                    event.deferReply().queue();
                    if (identifiers.isEmpty()) {
                        MusicManager.getInstance().queue(identifier, author, event.getHook());
                    } else {
                        //Chain queue multiple tracks
                    }
                }
            },

            new BotCommand(Commands
                    .slash("skip", "Skips the current song")
                    .setGuildOnly(true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
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
                public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
                    int tracksPerPage = 10;

                    TrackQueue queue = Checks.isPlaying(
                            Objects.requireNonNull(event.getGuild()).getId());

                    OptionMapping pageOption = event.getOption("page");
                    List<MusicTrack> musicTracks = new ArrayList<>();
                    musicTracks.add(queue.getCurrentlyPlaying());
                    musicTracks.addAll(queue.getTracks());
                    int lastPage = Math.floorDiv(musicTracks.size(), tracksPerPage) + 1;
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

    private static String getYouTubeSearch(Track spotifyTrack) {
        return YOUTUBE_SEARCH
                + spotifyTrack.getName()
                + " " + String.join(" ",
                Arrays.stream(spotifyTrack.getArtists())
                        .map(ArtistSimplified::getName)
                        .toList())
                + " " + YOUTUBE_SONG_FILTER;
    }

    private static void joinVoiceOfMember(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CommandFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        Guild memberGuild = memberToJoin.getGuild();
        AudioManager audioManager = memberGuild.getAudioManager();
        Checks.botNotConnected(audioManager);

        AudioPlayer player = MusicManager.getInstance().createPlayer(playerChannel);
        audioManager.setSendingHandler(new LavaPlayerJdaWrapper(player));
        audioManager.openAudioConnection(audioChannel);
    }

    private static void joinIfNotConnected(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CommandFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        try {
            joinVoiceOfMember(memberToJoin, playerChannel);
        } catch (CommandFailedException cfe) {
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
        } catch (CommandFailedException cfe) {
            //Do nothing.
        }
    }
}
