package org.smileyface.components.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.modals.ModalManager;

/**
 * Goes to a specific page in the queue.
 */
public class GoToPageButton extends CommandButton {
    public GoToPageButton() {
        super(ButtonStyle.SECONDARY, "goToPageButton", "Go To Page");
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        event.replyModal(ModalManager.getInstance().getItem("goToPageModal")).queue();
    }
}
