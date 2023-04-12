package org.smileyface.modals;

import java.util.stream.Stream;
import org.smileyface.generics.GenericManager;

public class ModalManager extends GenericManager<CommandModal> {
    private static ModalManager instance;

    private ModalManager() {
        super(Stream.of(
                new QueueSongModal()
        ), commandModal -> commandModal.getModal().getId());
    }

    public static ModalManager getInstance() {
        if (instance == null) {
            instance = new ModalManager();
        }
        return instance;
    }
}
