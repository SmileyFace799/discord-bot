package org.smileyface.modals;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.smileyface.commands.music.Music;
import org.smileyface.commands.music.PlayCommand;
import org.smileyface.misc.MultiTypeMap;

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
                        PlayCommand.ArgKeys.INPUT,
                        "URL / YouTube search query",
                        TextInputStyle.SHORT
                ).build(),
                TextInput.create(
                        PlayCommand.ArgKeys.SONG_SEARCH,
                        "Search only for songs? (Default: False)",
                        TextInputStyle.SHORT
                ).setPlaceholder(
                        "y/n, yes/no, true/false (case-insensitive)"
                ).setRequired(false).build()
        ));
    }

    @Override
    public void submitted(ModalInteractionEvent event) {
        ModalMapping songSearch = event.getValue(PlayCommand.ArgKeys.SONG_SEARCH);

        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(PlayCommand.ArgKeys.SONG_SEARCH, Objects.requireNonNull(
                event.getValue(PlayCommand.ArgKeys.INPUT)
        ).getAsString());
        args.put(PlayCommand.ArgKeys.INPUT, songSearch != null
                && Stream.of("y", "yes", "true")
                .anyMatch(yes -> yes.equalsIgnoreCase(songSearch.getAsString()))
        );

        Music.getInstance().getItem("play", PlayCommand.class).run(event, args);
    }
}
