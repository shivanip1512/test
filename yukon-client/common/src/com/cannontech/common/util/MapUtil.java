package com.cannontech.common.util;

import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class MapUtil {

    public static <K, V> ListMultimap<K, V> filterMultimap(
            ListMultimap<? extends K, ? extends V> inputMap,
            Predicate<? super V> predicate) {
        ListMultimap<K, V> result = ArrayListMultimap.create();

        // Create a new map with only creatable paoDefinitions in it
        for (Entry<? extends K, ? extends V> entry : inputMap.entries()) {
            if (predicate.apply(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
