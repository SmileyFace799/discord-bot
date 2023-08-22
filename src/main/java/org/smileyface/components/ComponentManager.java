package org.smileyface.components;

import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.smileyface.components.buttons.QueueButton;
import org.smileyface.generics.GenericManager;

/**
 * Intermediary class that instantiates & stores specific components.
 */
public class ComponentManager extends GenericManager<ActionComponent> {
    private static ComponentManager instance;

    private ComponentManager() {
        super(Stream.of(
                new QueueButton()
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

    /**
     * Gets a component by it's ID, and casts it to a specified type.
     *
     * @param componentId The ID of the component to get
     * @param componentType A class representing the type to cast the component to.
     *                      Must extend {@link ActionComponent}.
     * @param <T> The type to cast the component to
     * @return The component associated with the provided ID, casted to the specified type
     */
    public <T extends ActionComponent> T getItem(String componentId, Class<T> componentType) {
        return componentType.cast(getItem(componentId));
    }
}
