package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when the pause state of the player has changed.
 * There is no guarantee that the new value is different from the old one.
 *
 * @param paused   If the player is now paused or not
 * @param pausedBy The member that paused or un-paused the player
 */
public record PauseChangedEvent(boolean paused, Member pausedBy) implements QueueEvent {
}