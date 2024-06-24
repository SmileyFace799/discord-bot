package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when tracks are removed from the queue.
 *
 * @param removedBy The member who removed the tracks
 */
public record TracksRemovedEvent(Member removedBy) implements QueueEvent {}