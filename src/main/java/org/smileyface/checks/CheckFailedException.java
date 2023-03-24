package org.smileyface.checks;

/**
 * Thrown when a check on the bot's state fails.
 */
public class CheckFailedException extends Exception {
    public CheckFailedException(String message) {
        super(message);
    }
}
