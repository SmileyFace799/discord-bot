package org.smileyface.modals;

import java.util.stream.Stream;
import org.smileyface.generics.GenericManager;

/**
 * Intermediary class that instantiates & stores specific modals.
 */
public class ModalManager extends GenericManager<CommandModal> {
    private static ModalManager instance;

    private ModalManager() {
        super(Stream.of(
                new QueueSongModal()
        ), commandModal -> commandModal.getModal().getId());
    }

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static ModalManager getInstance() {
        if (instance == null) {
            instance = new ModalManager();
        }
        return instance;
    }
}
