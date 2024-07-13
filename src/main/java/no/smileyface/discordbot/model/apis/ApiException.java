package no.smileyface.discordbot.model.apis;

import org.jetbrains.annotations.NotNull;

/**
 * An exception relating to the state of the API. Will always have a cause.
 */
public class ApiException extends Exception {
	/**
	 * Constructor.
	 *
	 * @param message The exception message
	 * @param cause The cause of this API exception
	 */
	public ApiException(String message, @NotNull Throwable cause) {
		super(message, cause);
	}
}
