package no.smileyface.discordbot.model.querying;

import java.net.URL;
import java.util.Arrays;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

/**
 * Utility class with various methods used for parsing queries.
 */
public class QueryUtil {
	public static final String YOUTUBE_SEARCH = "ytsearch:";
	public static final String YOUTUBE_SONG_FILTER = " \"auto-generated by YouTube\"";

	private QueryUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String endOfPath(URL url) {
		String[] splitPath = url.getPath().split("/");
		return splitPath[splitPath.length - 1];
	}

	/**
	 * Converts track details into a YouTube search query.
	 *
	 * @param trackName The name of the track
	 * @param artistNames The name of any artists on the track
	 * @return The created YOuTube search query
	 */
	public static String toYouTubeSearchQuery(String trackName, String... artistNames) {
		return String.format("%s%s %s %s",
				YOUTUBE_SEARCH,
				trackName,
				String.join(" ", artistNames),
				YOUTUBE_SONG_FILTER
				);
	}

	/**
	 * Converts track details into a YouTube search query.
	 *
	 * @param trackName The name of the track
	 * @param artists Array of Spotify artists on the track
	 * @return The created YOuTube search query
	 */
	public static String toYouTubeSearchQuery(String trackName, ArtistSimplified[] artists) {
		return QueryUtil.toYouTubeSearchQuery(
				trackName,
				Arrays.stream(artists)
						.map(ArtistSimplified::getName)
						.toArray(String[]::new)
		);
	}
}
