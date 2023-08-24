package org.smileyface.components;

import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.smileyface.components.buttons.PlayPauseButton;
import org.smileyface.components.buttons.QueueButton;
import org.smileyface.generics.GenericManager;

/**
 * Intermediary class that instantiates & stores specific components.
 */
public class ComponentManager extends GenericManager<ActionComponent> {
    private static ComponentManager instance;

    private ComponentManager() {
        super(Stream.of(
                new QueueButton(),
                new PlayPauseButton()
        ), ActionComponent::getId);
    }

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static ComponentManager getInstance() {
        if (instance == null) {
            instance = new ComponentManager();
        }
        return instance;
    }
}
