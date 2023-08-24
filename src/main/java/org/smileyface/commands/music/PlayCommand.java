package org.smileyface.commands.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.hc.core5.http.ParseException;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.SpotifyManager;
import org.smileyface.misc.MultiTypeMap;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

/**
 * Command that plays music.
 */
public class PlayCommand extends BotCommand {
    private static final String YOUTUBE_SEARCH = "ytsearch:";
    private static final String YOUTUBE_SONG_FILTER = " \"auto-generated by YouTube\"";

    /**
     * Makes the play command.
     */
    public PlayCommand() {
        super(Commands
                        .slash("play", "Plays a song")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, ArgKeys.INPUT,
                                "A search or URL for the song to play. "
                                        + "Search is done through YouTube", true)
                        .addOption(OptionType.BOOLEAN, ArgKeys.SONG_SEARCH.toLowerCase(),
                                "If the YouTube search should search for songs only. "
                                        + "Ignored if input is a URL  (Default: False)"),
                Set.of("p")
        );
    }

    private static String getYouTubeSearch(String name, ArtistSimplified[] artists) {
        return YOUTUBE_SEARCH
                + name
                + " " + (artists == null ? "" : String.join(" ",
                Arrays.stream(artists)
                        .map(ArtistSimplified::getName)
                        .toList()))
                + " " + YOUTUBE_SONG_FILTER;
    }

    private static String getSpotifyTrackSearch(SpotifyApi api, String spotifyLink)
            throws IOException, SpotifyWebApiException, ParseException {
        Track track = api
                .getTrack(spotifyLink.split("track/")[1].split("\\?")[0])
                .build()
                .execute();
        return getYouTubeSearch(track.getName(), track.getArtists());
    }

    private static List<String> getSpotifyAlbumSearches(SpotifyApi api, String spotifyLink)
            throws IOException, SpotifyWebApiException, ParseException {
        List<String> links = new ArrayList<>();
        String next = spotifyLink.split("album/")[1].split("\\?")[0];
        int offset = 0;
        int limit = 50;
        while (next != null) {
            Paging<TrackSimplified> page = api
                    .getAlbumsTracks(next)
                    .offset(offset)
                    .limit(limit)
                    .build()
                    .execute();
            String nextUrl = page.getNext();
            next = nextUrl == null ? null
                    : nextUrl.split("albums/")[1].split("/")[0];
            if (next != null) {
                offset = Integer.parseInt(nextUrl.split(
                        "offset=")[1].split("&")[0]);
                limit = Integer.parseInt(nextUrl.split(
                        "limit=")[1].split("&")[0]);
            }
            links.addAll(Arrays.stream(page
                            .getItems())
                    .map(track -> getYouTubeSearch(track.getName(), track.getArtists()))
                    .toList()
            );
        }
        return links;
    }

    private static List<String> getSpotifyPlaylistSearches(SpotifyApi api, String spotifyLink)
            throws IOException, SpotifyWebApiException, ParseException {
        List<String> links = new ArrayList<>();
        String next = spotifyLink.split("playlist/")[1].split("\\?")[0];
        int offset = 0;
        int limit = 100;
        while (next != null) {
            Paging<PlaylistTrack> page = api.getPlaylistsItems(next)
                    .offset(offset)
                    .limit(limit)
                    .build()
                    .execute();
            String nextUrl = page.getNext();
            next = nextUrl == null ? null
                    : nextUrl.split("playlists/")[1].split("/")[0];
            if (next != null) {
                offset = Integer.parseInt(nextUrl.split(
                        "offset=")[1].split("&")[0]);
                limit = Integer.parseInt(nextUrl.split(
                        "limit=")[1].split("&")[0]);
            }
            links.addAll(Arrays
                    .stream(page.getItems())
                    .map(track -> {
                        String youtubeSearch;
                        try {
                            Track fullTrack =
                                    api.getTrack(track.getTrack().getId()).build().execute();
                            youtubeSearch = getYouTubeSearch(
                                    fullTrack.getName(), fullTrack.getArtists());
                        } catch (IOException | SpotifyWebApiException | ParseException e) {
                            youtubeSearch = getYouTubeSearch(track.getTrack().getName(), null);
                        }
                        return youtubeSearch;
                    }).toList()
            );
        }
        return links;
    }

    private static List<String> spotifyToYouTubeSearch(String spotifyLink) {
        List<String> links = new ArrayList<>();
        try {
            SpotifyApi api = SpotifyManager.getInstance().getApi();
            if (api == null) {
                throw new IllegalStateException("Spotify is not supported");
            }
            if (spotifyLink.contains("track/")) {
                links.add(getSpotifyTrackSearch(api, spotifyLink));
            } else if (spotifyLink.contains("album/")) {
                links.addAll(getSpotifyAlbumSearches(api, spotifyLink));
            } else if (spotifyLink.contains("playlist/")) {
                links.addAll(getSpotifyPlaylistSearches(api, spotifyLink));
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new IllegalStateException("Spotify error: "
                    + e.getMessage());
        }
        return links;
    }

    private static List<String> getIdentifiers(String[] links) {
        List<String> identifiers = new ArrayList<>();
        for (String link : links) {
            if (
                    link.toLowerCase().contains("youtube.com")
                            && link.contains("v=")
                            && link.contains("list=")
            ) {
                //If it's a YouTube video in a playlist, only play that video
                identifiers.add("https://www.youtube.com/watch?v="
                        + link.split("v=")[1].split("&")[0]);
            } else if (link.contains("open.spotify.com")) {
                identifiers.addAll(spotifyToYouTubeSearch(link));
            } else {
                identifiers.add(link);
            }
        }
        return identifiers;
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.authorInGuild(event);
        Checks.authorInVoice(event);
    }

    @Override
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(ArgKeys.INPUT, event.getOption(ArgKeys.INPUT, OptionMapping::getAsString));
        args.put(ArgKeys.SONG_SEARCH, event.getOption(
                ArgKeys.SONG_SEARCH.toLowerCase(),
                false, OptionMapping::getAsBoolean
        ));
        return args;
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        event.deferReply().setEphemeral(true).queue();
        Member author = Objects.requireNonNull(event.getMember());
        String input = args.get(ArgKeys.INPUT, String.class);
        boolean songSearch = args.get(ArgKeys.SONG_SEARCH, Boolean.class);
        List<String> identifiers = new ArrayList<>();

        String[] splitInput = input.replace("  ", " ").split(" ");
        try {
            if (Arrays.stream(splitInput).allMatch(identifier ->
                    identifier.startsWith("https://") || identifier.startsWith("http://"))
            ) {
                identifiers.addAll(getIdentifiers(splitInput));
            } else {
                String search = YOUTUBE_SEARCH + input;

                if (songSearch) {
                    search += YOUTUBE_SONG_FILTER;
                }
                identifiers.add(search);
            }

            JoinCommand.joinSilently(
                    Objects.requireNonNull(
                            Objects.requireNonNull(author.getVoiceState()).getChannel()
                    ),
                    (GuildMessageChannel) event.getMessageChannel()
            );
            if (identifiers.size() == 1) {
                MusicManager.getInstance().queue(identifiers.get(0), author, event.getHook());
            } else {
                MusicManager.getInstance().queueMultiple(identifiers, author, event.getHook());
            }
        } catch (IllegalStateException ise) {
            event.reply(ise.getMessage()).queue();
        }
    }

    /**
     * Keys for args map.
     */
    public static class ArgKeys {
        public static final String INPUT = "input";
        public static final String SONG_SEARCH = "songSearch";

        private ArgKeys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
