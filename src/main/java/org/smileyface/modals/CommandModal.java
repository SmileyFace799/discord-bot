package org.smileyface.modals;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.smileyface.checks.CommandFailedException;

public abstract class CommandModal {
    private Modal modal;

    protected CommandModal(Modal modal) {
        this.modal = modal;
    }

    public Modal getModal() {
        return modal;
    }

    public abstract void submitted(ModalInteractionEvent event) throws CommandFailedException;
}
