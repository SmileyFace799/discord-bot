package org.smileyface.commands.music;

import java.io.IOException;
import java.util.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.hc.core5.http.ParseException;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.SpotifyManager;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;

public class PlayCommand extends BotCommand {
    private static final String YOUTUBE_SEARCH = "ytsearch:";
    private static final String YOUTUBE_SONG_FILTER = " \"auto-generated by YouTube\"";

    public PlayCommand() {
        super(Commands
                        .slash("play", "Plays a song")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "input",
                                "A search or URL for the song to play. "
                                        + "Search is done through YouTube", true)
                        .addOption(OptionType.BOOLEAN, "songsearch",
                                "If the YouTube search should search for songs only. "
                                        + "Ignored if input is a URL  (Default: False)"),
                Set.of("p")
        );
    }

    private String getYouTubeSearch(String name, ArtistSimplified[] artists) {
        return YOUTUBE_SEARCH
                + name
                + " " + (artists == null ? "" : String.join(" ",
                Arrays.stream(artists)
                        .map(ArtistSimplified::getName)
                        .toList()))
                + " " + YOUTUBE_SONG_FILTER;
    }

    private String getSpotifyTrackSearch(SpotifyApi api, String spotifyLink)
            throws IOException, SpotifyWebApiException, ParseException {
        Track track = api
                .getTrack(spotifyLink.split("track/")[1].split("\\?")[0])
                .build()
                .execute();
        return getYouTubeSearch(track.getName(), track.getArtists());
    }

    private List<String> getSpotifyAlbumSearches(SpotifyApi api, String spotifyLink)
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

    private List<String> getSpotifyPlaylistSearches(SpotifyApi api, String spotifyLink)
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

    private List<String> spotifyToYouTubeSearch(String spotifyLink) throws CommandFailedException {
        List<String> links = new ArrayList<>();
        try {
            SpotifyApi api = SpotifyManager.getInstance().getApi();
            if (api == null) {
                throw new CommandFailedException("Spotify is not supported");
            }
            if (spotifyLink.contains("track/")) {
                links.add(getSpotifyTrackSearch(api, spotifyLink));
            } else if (spotifyLink.contains("album/")) {
                links.addAll(getSpotifyAlbumSearches(api, spotifyLink));
            } else if (spotifyLink.contains("playlist/")) {
                links.addAll(getSpotifyPlaylistSearches(api, spotifyLink));
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new CommandFailedException("Spotify error: "
                    + e.getMessage());
        }
        return links;
    }

    private List<String> getIdentifiers(String[] links) throws CommandFailedException {
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
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        event.deferReply().setEphemeral(true).queue();
        Member author = Objects.requireNonNull(event.getMember());
        Music.joinIfNotConnected(author, event.getGuildChannel());

        List<String> identifiers = new ArrayList<>();

        String input = Objects.requireNonNull(
                event.getOption("input")).getAsString();
        String[] splitInput = input.replace("  ", " ").split(" ");
        if (Arrays.stream(splitInput).allMatch(identifier ->
                identifier.startsWith("https://") || identifier.startsWith("http://"))
        ) {
            identifiers.addAll(getIdentifiers(splitInput));
        } else {
            String search = YOUTUBE_SEARCH + input;
            OptionMapping songSearchOption = event.getOption("songsearch");
            if (songSearchOption != null && songSearchOption.getAsBoolean()) {
                search += YOUTUBE_SONG_FILTER;
            }
            identifiers.add(search);
        }
        if (identifiers.size() == 1) {
            MusicManager.getInstance().queue(identifiers.get(0), author, event.getHook());
        } else {
            MusicManager.getInstance().queueMultiple(identifiers, author, event.getHook());
        }
    }
}
