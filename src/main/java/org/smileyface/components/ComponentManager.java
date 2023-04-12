package org.smileyface.components;

import java.util.stream.Stream;
import org.smileyface.components.buttons.QueueButton;
import org.smileyface.generics.GenericManager;

public class ComponentManager extends GenericManager<CommandComponent> {
    private static ComponentManager instance;
    private ComponentManager() {
        super(Stream.of(
                new QueueButton()
        ), commandComponent -> commandComponent.getComponent().getId());
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

    public <T extends CommandComponent> T getItem(String componentId, Class<T> componentType) {
        return componentType.cast(getItem(componentId));
    }
}
