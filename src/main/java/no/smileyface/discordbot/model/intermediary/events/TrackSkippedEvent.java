package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when the one or more songs are skipped.
 *
 * @param skippedBy The member who skipped the song(s)
 * @param amountSkipped How many songs were skipped
 */
public record TrackSkippedEvent(Member skippedBy, int amountSkipped) implements QueueEvent {}
