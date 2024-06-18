package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when the player is stopped.
 *
 * @param stoppedBy The member who stopped the player.
 *                  If the player stopped from natural causes, this is {@code null}
 */
public record PlayerStoppedEvent(Member stoppedBy) implements QueueEvent {}
