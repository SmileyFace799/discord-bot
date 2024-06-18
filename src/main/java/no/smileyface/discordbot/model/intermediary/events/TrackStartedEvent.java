package no.smileyface.discordbot.model.intermediary.events;

import no.smileyface.discordbot.model.MusicTrack;

/**
 * Fired when a new music track starts.
 *
 * @param startedTrack The track that started
 */
public record TrackStartedEvent(MusicTrack startedTrack) implements QueueEvent {}