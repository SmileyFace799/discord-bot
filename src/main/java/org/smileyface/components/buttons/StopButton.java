package org.smileyface.components.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.commands.music.Music;

/**
 * Button shortcut for {@code /stop}.
 */
public class StopButton extends CommandButton {
    public StopButton() {
        super(ButtonStyle.DANGER, "stopButton", "Stop");
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        Music.getInstance().getItem("leave").run(event);
    }
}
