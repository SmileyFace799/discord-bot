package org.smileyface.components;

import net.dv8tion.jda.api.interactions.components.ActionComponent;

public abstract class CommandComponent {
    private ActionComponent component;

    protected CommandComponent(ActionComponent component) {
        this.component = component;
    }

    public ActionComponent getComponent() {
        return component;
    }
}
