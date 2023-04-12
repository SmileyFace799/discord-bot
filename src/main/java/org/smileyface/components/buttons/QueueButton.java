package org.smileyface.components.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.smileyface.modals.ModalManager;

public class QueueButton extends CommandButton {
    public QueueButton() {
        super(Button.primary("testButton", "Queue more"));
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        event.replyModal(ModalManager
                .getInstance()
                .getItem("queueSong")
                .getModal()).queue();
    }
}
