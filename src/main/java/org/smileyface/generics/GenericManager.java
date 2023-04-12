package org.smileyface.generics;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericManager<T> {
    private Map<String, T> itemMap;

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

    public Map<String, T> getItems() {
        return itemMap;
    }
}
