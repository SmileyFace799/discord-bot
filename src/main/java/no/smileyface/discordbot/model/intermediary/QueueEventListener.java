package no.smileyface.discordbot.model.intermediary;

import no.smileyface.discordbot.model.intermediary.events.QueueEvent;

/**
 * Generic listener for music events.
 */
public interface QueueEventListener {
	void onMusicEvent(QueueEvent event);
}
