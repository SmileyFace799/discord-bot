package no.smileyface.discordbot.model.apis;

import java.io.IOException;
import java.time.LocalDateTime;
import no.smileyface.discordbot.files.properties.PropertyNode;
import no.smileyface.discordbot.model.apis.generic.ApiConsumer;
import no.smileyface.discordbot.model.apis.generic.ApiContainer;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;

/**
 * Container for the spotify API.
 */
public class SpotifyContainer implements ApiContainer<SpotifyApi> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyContainer.class);

    private final SpotifyApi api;
    private LocalDateTime tokenExpiry = null;

    /**
     * Constructor.
     */
    public SpotifyContainer() {
        PropertyNode spotifyNode = PropertyNode.getRoot()
                .getChild("token")
                .getChild("spotify");
        String id = spotifyNode.getChild("id").getValue();
        String secret = spotifyNode.getChild("secret").getValue();
        if (id == null || secret == null) {
            LOGGER.warn("No authentication found for the Spotify API");
            api = null;
        } else {
            api = new SpotifyApi.Builder()
                    .setClientId(id)
                    .setClientSecret(secret)
                    .build();
            refreshAccessToken();
        }
    }

    private void refreshAccessToken() {
        try {
            ClientCredentials clientCredentials =
                    api.clientCredentials().build().execute();
            api.setAccessToken(clientCredentials.getAccessToken());
            tokenExpiry = LocalDateTime.now()
                    .plusSeconds((long) clientCredentials.getExpiresIn() - 60); //1min margin
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.warn("Spotify authorization error", e);
        }
    }

    @Override
    public void withApi(
            ApiConsumer<SpotifyApi> apiConsumer
    ) throws ApiException, UnsupportedApiException {
        if (api == null) {
            throw new UnsupportedApiException("Spotify is not supported");
        } else if (LocalDateTime.now().isAfter(tokenExpiry)) {
            refreshAccessToken();
        }
        apiConsumer.accept(api);
    }
}
