package org.smileyface.modals;

import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.smileyface.audio.MusicManager;

/**
 * Go to specified queue page.
 */
public class GoToPageModal extends CommandModal {
    /**
     * Makes the modal.
     */
    public GoToPageModal() {
        super("goToPageModal", "Go To Page...", List.of(
                TextInput.create("page", "Page", TextInputStyle.SHORT).build()
        ));
    }

    @Override
    public void submitted(ModalInteractionEvent event) {
        String rawPage = Objects.requireNonNull(event.getValue("page")).getAsString();
        try {
            int page = Integer.parseInt(rawPage);
            MusicManager.getInstance()
                    .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                    .getTrackQueueMessage()
                    .setPage(page);
            event.reply(String.format("Showing page %s!", page)).setEphemeral(true).queue();
        } catch (NumberFormatException nfe) {
            event.reply(String.format("\"%s\" is not a valid number", rawPage))
                    .setEphemeral(true).queue();
        }
    }
}
