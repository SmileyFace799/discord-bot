package no.smileyface.discordbot.model.intermediary.events;

import java.util.function.BiConsumer;

/**
 * Fired when the queue page to show is incremented or decremented.
 *
 * @param increment If the page should be incremented. Will be decremented otherwise
 * @param callback  A callback with the new page, and if the page was actually changed
 */
public record ChangePageEvent(
		boolean increment,
		BiConsumer<Integer, Boolean> callback
) implements QueueEvent {}
