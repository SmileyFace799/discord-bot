package org.smileyface.checks;

/**
 * Thrown when a check on the bot's state fails.
 */
public class CommandFailedException extends Exception {
    public CommandFailedException(String message) {
        super(message);
    }
}
