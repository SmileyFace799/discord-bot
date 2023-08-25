package org.smileyface.components.buttons;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.commands.categories.Music;
import org.smileyface.commands.music.RepeatCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Shortcut for {@code /repeat [...]}.
 */
public class RepeatButton extends CommandButton {
    public RepeatButton() {
        super(ButtonStyle.PRIMARY, "repeatButton", "Repeat Song / Queue / Off");
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        String key = RepeatCommand.ArgKeys.REPEAT_MODE;
        switch (MusicManager.getInstance()
                .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getRepeat()
        ) {
            case NO_REPEAT -> args.put(key, TrackQueue.Repeat.REPEAT_SONG);
            case REPEAT_SONG -> args.put(key, TrackQueue.Repeat.REPEAT_QUEUE);
            default -> args.put(key, TrackQueue.Repeat.NO_REPEAT);
        }
        Music.getInstance().getItem("repeat").run(event, args);
    }
}
