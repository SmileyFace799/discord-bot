package org.smileyface.commands;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.Nullable;
import org.smileyface.TokenManager;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;

/**
 * Manages the spotify API.
 */
public class SpotifyManager {
    private static SpotifyManager instance;

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized SpotifyManager getInstance() {
        if (instance == null) {
            instance = new SpotifyManager();
        }
        return instance;
    }

    private final SpotifyApi api;
    private LocalDateTime tokenExpiry = null;

    private SpotifyManager() {
        SpotifyApi spotifyApi;
        try {
            String[] spotifyClientInfo = TokenManager.getSpotify();
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(spotifyClientInfo[0])
                    .setClientSecret(spotifyClientInfo[1])
                    .build();
        } catch (NoSuchFileException nsfe) {
            System.out.println(nsfe.getMessage());
            spotifyApi = null;
        }

        api = spotifyApi;
        if (api != null) {
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
            System.out.println("Spotify authorization error: " + e.getMessage());
        }
    }

    /**
     * Gets the spotify API. Will be null if Spotify functionality is not enabled.
     *
     * @return The spotify API object, if Spotify functionality is enabled
     */
    public synchronized @Nullable SpotifyApi getApi() {
        if (api != null && LocalDateTime.now().isAfter(tokenExpiry)) {
            refreshAccessToken();
        }
        return api;
    }
}
