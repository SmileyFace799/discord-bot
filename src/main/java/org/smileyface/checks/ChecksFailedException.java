package org.smileyface.checks;

/**
 * Thrown when a check on the bot's state fails.
 */
public class ChecksFailedException extends Exception {
    public ChecksFailedException(String message) {
        super(message);
    }
}
