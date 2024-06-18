package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when the shuffle state of the player has changed.
 * There is no guarantee that the new value is different from the old one.
 *
 * @param shuffled   If the player is now shuffled or not
 * @param shuffledBy The member who shuffled / un-shuffled the player
 */
public record ShuffleChangedEvent(boolean shuffled, Member shuffledBy) implements QueueEvent {
}