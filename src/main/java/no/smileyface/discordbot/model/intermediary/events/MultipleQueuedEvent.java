package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when multiple tracks or playlists were queued at once.
 *
 * @param queuedBy The member who queued the tracks / playlists
 */
public record MultipleQueuedEvent(Member queuedBy) implements QueueEvent {}