package org.smileyface.components.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.commands.categories.Music;

/**
 * Shortcut for {@code /shuffle}.
 */
public class ShuffleButton extends CommandButton {
    public ShuffleButton() {
        super(ButtonStyle.PRIMARY, "shuffleButton", "Shuffle");
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        Music.getInstance().getItem("shuffle").run(event);
    }
}
