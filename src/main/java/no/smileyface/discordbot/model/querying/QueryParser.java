package no.smileyface.discordbot.model.querying;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.smileyface.discordbot.model.apis.DeezerContainer;
import no.smileyface.discordbot.model.apis.SpotifyContainer;
import no.smileyface.discordbot.model.apis.generic.ApiContainer;

/**
 * Parses various URLs into queueable queries.
 */
public class QueryParser {
	private final SpotifyContainer spotify;
	private final DeezerContainer deezer;
	private final Map<String, ApiContainer<?>> apiNameReference;

	/**
	 * Constructor.
	 */
	public QueryParser() {
		this.spotify = new SpotifyContainer();
		this.deezer = new DeezerContainer();
		this.apiNameReference = Stream
				.of(spotify, deezer)
				.collect(Collectors.toMap(
						api -> api.getClass().getName(),
						Function.identity()
				));
	}

	private static URL getUrl(String link) throws MalformedURLException {
		return URI.create(link).toURL();
	}

	private static URL redirectUrl(URL url) {
		URL redirectedUrl;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			redirectedUrl = URI.create(conn.getHeaderField("location")).toURL();
		} catch (IOException ioe) {
			redirectedUrl = null;
		}
		return redirectedUrl;
	}

	public boolean isSupported(Class<? extends ApiContainer<?>> apiClass) {
		String name = apiClass.getName();
		return apiNameReference.containsKey(name) && apiNameReference.get(name).isSupported();
	}

	private List<Query> getQueriesFromUrl(URL url, String originalQuery) {
		List<Query> queries;
		if (url == null) {
			queries = List.of(new QueryError(
					originalQuery,
					"The provided URL redirects somewhere, "
							+ "but its destination cannot be resolved"
			));
		} else {
			queries = switch (url.getHost()) {
				case "open.spotify.com" -> SpotifyParser.parse(url, spotify);
				case "www.deezer.com" -> DeezerParser.parse(url, deezer);
				case "www.youtube.com" -> List.of(new Query(url.toString()));
				default -> List.of(new QueryError(originalQuery, "Unsupported website"));
			};
		}
		return queries;
	}

	/**
	 * Gets any queries from an input.
	 *
	 * @param input The input to get queries from
	 * @return Any queries created from the input
	 */
	public List<Query> getQueries(String input) {
		List<Query> queries;
		try {
			URL url = getUrl(input);
			String queryParams = url.getQuery();
			if (url.getHost().equals("www.youtube.com")
					&& url.getPath().startsWith("/watch")
					&& queryParams.contains("v=")
					&& queryParams.contains("list=")
			) {
				url = getUrl("https://www.youtube.com/watch?v=)"
						+ queryParams.split("v=")[1].split("&")[0]
				);
			} else if (Set.of("deezer.page.link", "youtu.be").contains(url.getHost())) {
				url = redirectUrl(url);
			}
			queries = getQueriesFromUrl(url, input);
		} catch (IllegalArgumentException | MalformedURLException e) {
			queries = List.of(new Query(QueryUtil.YOUTUBE_SEARCH + input));
		}
		return queries;
	}
}
