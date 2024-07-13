package no.smileyface.discordbot.model.apis;

/**
 * Thrown when an API is not supported.
 */
public class UnsupportedApiException extends Exception {
	public UnsupportedApiException(String message) {
		super(message);
	}
}
