package org.smileyface.components.buttons;

import java.util.Objects;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.music.Music;

/**
 * Pauses / Resumes music when clicked.
 */
public class PlayPauseButton extends CommandButton {
    public PlayPauseButton() {
        super(ButtonStyle.PRIMARY, "playPauseButton", "Play / Pause", Emoji.fromUnicode("⏯"));
    }

    @Override
    public void clicked(ButtonInteractionEvent event) {
        if (!event.isFromGuild()) {
            return;
        }

        try {
            Checks.botConnectedToAuthorVoice(event);
            if (MusicManager.getInstance()
                    .getQueue(Objects.requireNonNull(event.getGuild())
                            .getIdLong()
                    ).getPlayer()
                    .isPaused()
            ) {
                Music.getInstance().getItem("resume").run(event);
            } else {
                Music.getInstance().getItem("pause").run(event);
            }
        } catch (ChecksFailedException cfe) {
            event.reply(cfe.getMessage()).setEphemeral(true).queue();
        }
    }
}
