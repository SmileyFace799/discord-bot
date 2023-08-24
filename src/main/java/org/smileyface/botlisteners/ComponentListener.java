package org.smileyface.botlisteners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.smileyface.components.ComponentManager;
import org.smileyface.components.buttons.CommandButton;
import org.smileyface.modals.ModalManager;

/**
 * Listens to & handles events from components.
 */
public class ComponentListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        ComponentManager
                .getInstance()
                .getItem(event.getComponentId(), CommandButton.class)
                .clicked(event);
    }

    @Override
    public void onGenericSelectMenuInteraction(@NotNull GenericSelectMenuInteractionEvent event) {
        //Not used, yet
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        ModalManager.getInstance()
                .getItem(event.getModalId())
                .submitted(event);
    }
}
