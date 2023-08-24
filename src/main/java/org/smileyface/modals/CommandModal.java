package org.smileyface.modals;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.internal.interactions.modal.ModalImpl;

/**
 * A modal that can be shown to the user & execute code when submitted.
 */
public abstract class CommandModal extends ModalImpl {

    protected CommandModal(String id, String title, List<ItemComponent> components) {
        super(id, title, components.stream().map(item ->
                (LayoutComponent) ActionRow.of(item)).toList());
    }

    /**
     * Submits a modal.
     *
     * @param event The invocation event
     */
    public abstract void submitted(ModalInteractionEvent event);
}
