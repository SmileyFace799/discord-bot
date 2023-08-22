package org.smileyface.components.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

/**
 * A button component that can be added to messages & execute code when clicked.
 */
public abstract class CommandButton extends ButtonImpl {

    protected CommandButton(ButtonStyle style, String id, String text) {
        super(id, text, style, false, null);
    }

    public abstract void clicked(ButtonInteractionEvent events);
}
