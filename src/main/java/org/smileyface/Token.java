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
public class Token {
    private Token() {
        throw new IllegalStateException("Utility class");
    }

    private static final String TOKENS_PATH = "tokens/";
    private static final String TOKEN_EXTENSION = ".token";

    /**
     * Gets a bot's token based on its name.
     *
     * @param botName The bot name to get the token for
     * @return The bot token
     * @throws NoSuchFileException If no token is found for the specified bot name.
     */
    public static String get(String botName) throws NoSuchFileException {
        Path tokenFile = Paths.get(TOKENS_PATH + botName + TOKEN_EXTENSION);
        String token;
        try (BufferedReader tokenReader =
                     Files.newBufferedReader(tokenFile, StandardCharsets.UTF_8)) {
            token = tokenReader.readLine();
        } catch (IOException ioe) {
            throw new NoSuchFileException("Could not find a token for any bot named \""
                    + botName + "\"");
        }
        return token;
    }
}
