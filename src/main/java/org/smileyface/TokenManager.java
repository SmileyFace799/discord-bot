package org.smileyface;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for fetching bot tokens.
 */
public class TokenManager {
    private static final String TOKENS_PATH = "tokens/";
    private static final String TOKEN_EXTENSION = ".token";

    private TokenManager() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets a bot's token based on its name.
     *
     * @param botName The bot name to get the token for
     * @return The bot token
     * @throws NoSuchFileException If no token is found for the specified bot name.
     */
    public static String getBot(String botName) throws NoSuchFileException {
        Path tokenFile = Paths.get(TOKENS_PATH + botName.toLowerCase() + TOKEN_EXTENSION);
        String token;
        try (BufferedReader tokenReader =
                     Files.newBufferedReader(tokenFile, StandardCharsets.UTF_8)) {
            token = tokenReader.readLine();
        } catch (IOException ioe) {
            throw new NoSuchFileException("Could not find a token for any bot named \""
                    + botName + "\". Make sure the token file only contains lower case letters");
        }
        return token;
    }

    /**
     * Gets the token of the active bot.
     * The active bot is the bot name located in an "activeBot.txt"-file,
     * in the same path as the application.
     *
     * @return The bot token
     * @throws NoSuchFileException If no active bot is found,
     *                             or no token is found for the active bot name.
     */
    public static String getActiveBot() throws NoSuchFileException {
        String name;
        try (BufferedReader nameReader =
                     Files.newBufferedReader(Paths.get("activeBot.txt"), StandardCharsets.UTF_8)) {
            name = nameReader.readLine();
        } catch (IOException ioe) {
            throw new NoSuchFileException("Could not find an active bot");
        }
        return getBot(name);
    }

    /**
     * Gets an array of strings, containing the following:
     * <ul>
     *     <li>A spotify client ID</li>
     *     <li>A spotify client secret</li>
     * </ul>
     * These are found within the "spotifyClient.sptoken"-file, in the "tokens"-folder.
     *
     * @return And array contain the spotify client ID & secret
     * @throws NoSuchFileException If the spotify client file cannot be found
     */
    public static String[] getSpotify() throws NoSuchFileException {
        String[] clientInfo = new String[2];
        try (BufferedReader nameReader = Files.newBufferedReader(
                Paths.get("tokens/spotifyClient.sptoken"), StandardCharsets.UTF_8)) {
            clientInfo[0] = nameReader.readLine();
            clientInfo[1] = nameReader.readLine();
        } catch (IOException ioe) {
            throw new NoSuchFileException("Could not find spotify client info. "
                    + "Spotify features will not be available");
        }
        return clientInfo;
    }
}
