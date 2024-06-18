package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;
import no.smileyface.discordbot.model.TrackQueue;

/**
 * Fired when the repeat state of the player has changed.
 * There is no guarantee that the new value is different from the old one.
 *
 * @param repeat    The player's new repeat state
 * @param changedBy The member that changed the repeat state
 */
public record RepeatChangedEvent(
		TrackQueue.Repeat repeat,
		Member changedBy
) implements QueueEvent {}
