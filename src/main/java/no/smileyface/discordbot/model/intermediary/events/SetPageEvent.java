package no.smileyface.discordbot.model.intermediary.events;

import java.util.function.IntConsumer;

/**
 * Fired when the queue page to show is set.
 *
 * @param page     The page to show
 * @param callback A callback with the actual page shown
 */
public record SetPageEvent(int page, IntConsumer callback) implements QueueEvent {
}
