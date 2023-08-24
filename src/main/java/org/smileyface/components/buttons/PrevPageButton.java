package org.smileyface.components.buttons;

import java.util.Objects;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueueMessage;

/**
 * Shows the next queue page.
 */
public class PrevPageButton extends CommandButton {
    public PrevPageButton() {
        super(ButtonStyle.PRIMARY, "prevPageButton", "Previous Page", Emoji.fromUnicode("◀"));
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        TrackQueueMessage message =
                MusicManager.getInstance()
                        .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                        .getTrackQueueMessage();

        event.reply(message.incrementPage(-1)
                ? String.format("Showing page %s!", message.getPage())
                : "Already showing the first page"
        ).setEphemeral(true).queue();
    }
}
