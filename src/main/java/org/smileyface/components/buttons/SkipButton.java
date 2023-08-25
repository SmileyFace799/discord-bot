package org.smileyface.components.buttons;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.commands.categories.Music;
import org.smileyface.misc.MultiTypeMap;

/**
 * Button shortcut for {@code /skip 1}.
 */
public class SkipButton extends CommandButton {
    public SkipButton() {
        super(ButtonStyle.PRIMARY, "skipButton", "Skip", Emoji.fromUnicode("⏩"));
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put("amount", 1);
        Music.getInstance().getItem("skip").run(event, args);
    }
}
