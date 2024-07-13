package no.smileyface.discordbot.model.querying;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.smileyface.discordbot.model.apis.ApiException;
import no.smileyface.discordbot.model.apis.SpotifyContainer;
import no.smileyface.discordbot.model.apis.UnsupportedApiException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

/**
 * A spotify track query.
 */
public class SpotifyParser {
	private SpotifyParser() {
		throw new IllegalStateException("Utility class");
	}

	private static Query track(URL url, SpotifyApi api) throws ApiException {
		Track track;
		try {
			track = api
					.getTrack(QueryUtil.endOfPath(url))
					.build()
					.execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			throw new ApiException(e.getMessage(), e);
		}
		return new Query(QueryUtil.toYouTubeSearchQuery(track.getName(), track.getArtists()));
	}

	private static List<Query> album(URL url, SpotifyApi api) throws ApiException {
		List<Query> queries = new ArrayList<>();
		String next = QueryUtil.endOfPath(url);
		int offset = 0;
		int limit = 50;
		while (next != null) {
			try {
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
				queries.addAll(Arrays.stream(page.getItems())
						.map(track -> new Query(QueryUtil.toYouTubeSearchQuery(
								track.getName(),
								Arrays.stream(track.getArtists())
										.map(ArtistSimplified::getName)
										.toArray(String[]::new)
						)))
						.toList()
				);
			} catch (IOException | SpotifyWebApiException | ParseException e) {
				throw new ApiException(e.getMessage(), e);
			}
		}
		return queries;
	}

	private static List<Query> playlist(URL url, SpotifyApi api) throws ApiException {
		List<Query> queries = new ArrayList<>();
		String next = QueryUtil.endOfPath(url);
		int offset = 0;
		int limit = 100;
		while (next != null) {
			try {
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
				queries.addAll(Arrays
						.stream(page.getItems())
						.map(track -> {
							String youtubeSearch;
							try {
								Track fullTrack = api
										.getTrack(track.getTrack().getId())
										.build()
										.execute();
								youtubeSearch = QueryUtil.toYouTubeSearchQuery(
										fullTrack.getName(),
										fullTrack.getArtists()
								);
							} catch (IOException | SpotifyWebApiException | ParseException e) {
								youtubeSearch = QueryUtil.toYouTubeSearchQuery(
										track.getTrack().getName(),
										new ArtistSimplified[]{}
								);
							}
							return new Query(youtubeSearch);
						}).toList()
				);
			} catch (IOException | SpotifyWebApiException | ParseException e) {
				throw new ApiException(e.getMessage(), e);
			}
		}
		return queries;
	}

	/**
	 * Parses a Spotify URL into one or more {@link Query}s.
	 *
	 * @param url The URL to parse
	 * @param spotify The Spotify API
	 * @return Parsed queries from the URL
	 */
	public static List<Query> parse(URL url, SpotifyContainer spotify) {
		List<Query> queries = new ArrayList<>();
		try {
			spotify.withApi(api -> {
				String path = url.getPath();
				if (path.startsWith("/album")) {
					queries.addAll(album(url, api));
				} else if (path.startsWith("/playlist")) {
					queries.addAll(playlist(url, api));
				} else if (path.startsWith("/track")) {
					queries.add(track(url, api));
				} else {
					queries.add(new QueryError(
							url.toString(),
							"This type of Spotify URL is not supported"
					));
				}
			});
		} catch (ApiException | UnsupportedApiException e) {
			queries.add(new QueryError(url.toString(), e.getMessage()));
		}
		return queries;
	}
}
