package org.smileyface.components.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.smileyface.components.CommandComponent;

public abstract class CommandButton extends CommandComponent {

    protected CommandButton(Button button) {
        super(button);
    }

    @Override
    public Button getComponent() {
        return (Button) super.getComponent();
    }

    public abstract void clicked(ButtonInteractionEvent events);
}
