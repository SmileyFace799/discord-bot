package org.smileyface.components;

import net.dv8tion.jda.api.interactions.components.ActionComponent;

/**
 * A generic component that can be added to messages.
 */
public abstract class CommandComponent {
    private final ActionComponent component;

    protected CommandComponent(ActionComponent component) {
        this.component = component;
    }

    public ActionComponent getComponent() {
        return component;
    }
}
