package no.smileyface.discordbot.model.intermediary.events;

import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when the bot joins the voice channel.
 *
 * @param createdBy The member who made the bot join
 */
public record BotJoinedEvent(Member createdBy) implements QueueEvent {}
