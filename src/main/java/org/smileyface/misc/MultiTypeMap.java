package org.smileyface.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Map that can store values of any type.
 *
 * @param <K> The key type
 */
public class MultiTypeMap<K> {
    private final Map<K, Object> valueMap;

    public MultiTypeMap() {
        valueMap = new HashMap<>();
    }

    public void put(K key, Object value) {
        valueMap.put(key, value);
    }

    public <V> V get(K key, Class<V> returnType) {
        return returnType.cast(valueMap.get(key));
    }
}
