package org.smileyface.modals;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.music.PlayCommand;

/**
 * Shown when {@link org.smileyface.components.buttons.QueueButton QueueButton} is clicked.
 */
public class QueueSongModal extends CommandModal {

    /**
     * Makes the queue song modal.
     */
    public QueueSongModal() {
        super("queueSong", "Queue a song / video / playlist", List.of(
                TextInput.create(
                        "input",
                        "URL / YouTube search query",
                        TextInputStyle.SHORT
                ).build(),
                TextInput.create(
                        "songSearch",
                        "Search only for songs? (Default: False)",
                        TextInputStyle.SHORT
                ).setPlaceholder(
                        "y/n, yes/no, true/false (case-insensitive)"
                ).setRequired(false).build()
        ));
    }

    @Override
    public void submitted(ModalInteractionEvent event) throws CommandFailedException {
        event.deferReply(true).queue();
        ModalMapping songSearch = event.getValue("songSearch");
        PlayCommand.playTrack(
                Objects.requireNonNull(event.getMember()),
                Objects.requireNonNull(event.getGuildChannel()),
                Objects.requireNonNull(event.getValue("input")).getAsString(),
                songSearch != null
                        && Stream.of("y", "yes", "true")
                        .anyMatch(yes -> yes.equalsIgnoreCase(songSearch.getAsString())),
                event.getHook());
    }
}
