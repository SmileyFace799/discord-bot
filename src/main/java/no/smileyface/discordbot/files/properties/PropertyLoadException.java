package no.smileyface.discordbot.files.properties;

/**
 * Thrown when something went wrong while loading the .properties file for the bot.
 */
public class PropertyLoadException extends Exception {
	public PropertyLoadException(String message) {
		super(message);
	}

	public PropertyLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
