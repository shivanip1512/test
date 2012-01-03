package com.cannontech.common.util;


import java.util.Map.Entry;
import java.util.SortedMap;

import com.google.common.base.Predicate;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public class MapUtil {

    public static <K, V> ListMultimap<K, V> filterLinkedListMultimap(
            ListMultimap<? extends K, ? extends V> inputMap,
            Predicate<? super V> predicate) {
        ListMultimap<K, V> result = LinkedListMultimap.create();

        // Put entries in map based on predicate application
        for (Entry<? extends K, ? extends V> entry : inputMap.entries()) {
            if (predicate.apply(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static <K, V> SortedMap<K, V> putFirstEntries(int max, SortedMap<K, V> source, SortedMap<K, V> target) {
        int count = 0;
        for (Entry<K, V> entry : source.entrySet()) {
            if (count >= max) break;

            target.put(entry.getKey(), entry.getValue());
            count++;
        }
        return target;
    }
}
