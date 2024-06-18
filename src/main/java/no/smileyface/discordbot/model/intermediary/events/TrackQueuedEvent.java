package no.smileyface.discordbot.model.intermediary.events;

import no.smileyface.discordbot.model.MusicTrack;

/**
 * Fired when a music track is queued.
 *
 * @param track The track that's queued
 */
public record TrackQueuedEvent(MusicTrack track) implements QueueEvent {}