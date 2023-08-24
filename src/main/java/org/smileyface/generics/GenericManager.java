package org.smileyface.generics;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

/**
 * Generic manager class that stores instantiated objects & associates them with keys.
 *
 * @param <T> The type of objects to store
 */
public class GenericManager<T> {
    private final Map<String, T> itemMap;

    protected GenericManager(Stream<T> items, Function<T, String> keyFunction) {
        itemMap = items.collect(Collectors.toMap(
                keyFunction,
                modal -> modal
        ));
    }

    protected GenericManager(Stream<Map.Entry<String, T>> entries) {
        itemMap = entries.collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }

    public T getItem(String key) {
        return itemMap.get(key);
    }

    /**
     * Gets a component by its ID, and casts it to a specified type.
     *
     * @param componentId The ID of the component to get
     * @param componentType A class representing the type to cast the component to.
     *                      Must extend {@link ActionComponent}.
     * @param <C> The type to cast the component to
     * @return The component associated with the provided ID, cast to the specified type
     */
    public <C extends T> C getItem(String componentId, Class<C> componentType) {
        return componentType.cast(getItem(componentId));
    }

    public Map<String, T> getItems() {
        return itemMap;
    }
}
