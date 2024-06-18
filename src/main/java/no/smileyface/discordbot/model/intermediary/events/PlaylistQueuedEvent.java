package no.smileyface.discordbot.model.intermediary.events;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import net.dv8tion.jda.api.entities.Member;

/**
 * Fired when a playlist is queued.
 *
 * @param playlist The playlist queued
 * @param queuedBy The member who queued the playlist
 */
public record PlaylistQueuedEvent(AudioPlaylist playlist, Member queuedBy) implements QueueEvent {}
