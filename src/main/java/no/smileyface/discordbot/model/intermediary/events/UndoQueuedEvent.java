package no.smileyface.discordbot.model.intermediary.events;

/**
 * Fired when some queued tracks where un-queued.
 */
public record UndoQueuedEvent() implements QueueEvent {}
