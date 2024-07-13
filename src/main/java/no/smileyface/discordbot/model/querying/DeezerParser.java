package no.smileyface.discordbot.model.querying;

import api.deezer.DeezerApi;
import api.deezer.exceptions.DeezerException;
import api.deezer.objects.Album;
import api.deezer.objects.Playlist;
import api.deezer.objects.Track;
import api.deezer.objects.data.TrackData;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.smileyface.discordbot.model.apis.ApiException;
import no.smileyface.discordbot.model.apis.DeezerContainer;

/**
 * A Deezer track query.
 */
public class DeezerParser {
	private DeezerParser() {
		throw new IllegalStateException("Utility class");
	}

	private static Query track(URL url, DeezerApi api) throws ApiException {
		Query query;
		try {
			Track track = api
					.track()
					.getById(Long.parseLong(QueryUtil.endOfPath(url)))
					.execute();
			query = new Query(QueryUtil.toYouTubeSearchQuery(
					track.getTitle(),
					track.getArtist().getName()
			));
		} catch (DeezerException de) {
			throw new ApiException(de.getMessage(), de);
		}
		return query;
	}

	private static List<Query> album(URL url, DeezerApi api) throws ApiException {
		List<Track> allTracks = new ArrayList<>();
		try {
			long albumId = Long.parseLong(QueryUtil.endOfPath(url));
			Album album = api.album().getById(albumId).execute();
			TrackData trackData = api.album().getTracks(albumId).execute();
			int trackPos = 0;
			while (trackPos < album.getNbTracks()) {
				List<Track> tracks = trackData.getData();
				allTracks.addAll(tracks);
				trackPos += tracks.size();
				if (trackPos < album.getNbTracks()) {
					trackData = api.album().getTracks(albumId).index(trackPos).execute();
				}
			}
		} catch (DeezerException de) {
			throw new ApiException(de.getMessage(), de);
		}
		return allTracks
				.stream()
				.map(track -> new Query(QueryUtil.toYouTubeSearchQuery(
						track.getTitle(),
						track.getArtist().getName()
				)))
				.toList();
	}

	private static List<Query> playlist(URL url, DeezerApi api) throws ApiException {
		List<Track> allTracks = new ArrayList<>();
		try {
			long playlistId = Long.parseLong(QueryUtil.endOfPath(url));
			Playlist playlist = api.playlist().getById(playlistId).execute();
			TrackData trackdata = api.playlist().getTracks(playlistId).execute();
			int trackPos = 0;
			while (trackPos < playlist.getNbTracks()) {
				List<Track> tracks = trackdata.getData();
				allTracks.addAll(tracks);
				trackPos += tracks.size();
				if (trackPos < playlist.getNbTracks()) {
					trackdata = api.playlist().getTracks(playlistId).index(trackPos).execute();
				}
			}
		} catch (DeezerException de) {
			throw new ApiException(de.getMessage(), de);
		}
		return allTracks
				.stream()
				.map(track -> new Query(QueryUtil.toYouTubeSearchQuery(
						track.getTitle(),
						track.getArtist().getName()
				)))
				.toList();
	}

	/**
	 * Parses a Deezer URL into one or more {@link Query}s.
	 *
	 * @param url The URL to parse
	 * @param deezer The Deezer API
	 * @return Parsed queries from the URL
	 */
	public static List<Query> parse(URL url, DeezerContainer deezer) {
		List<Query> queries = new ArrayList<>();
		try {
			deezer.withApi(api -> {
				List<String> pathSplit = Arrays.stream(url.getPath().split("/")).toList();
				if (pathSplit.contains("track")) {
					queries.add(track(url, api));
				} else if (pathSplit.contains("album")) {
					queries.addAll(album(url, api));
				} else if (pathSplit.contains("playlist")) {
					queries.addAll(playlist(url, api));
				} else {
					queries.add(new QueryError(
							url.toString(),
							"This type of Deezer URL is not supported"
					));
				}
			});
		} catch (ApiException ae) {
			queries.add(new QueryError(url.toString(), ae.getMessage()));
		}
		return queries;
	}
}
