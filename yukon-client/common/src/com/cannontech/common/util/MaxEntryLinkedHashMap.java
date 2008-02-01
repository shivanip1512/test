package com.cannontech.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MaxEntryLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    
    private Integer maxEntries;
    
    /**
     * LinkedHashMap that will remove eldest entries when size exceeds maxEntries.
     * Use null maxEntries to create a LinkedHashMap of unlimited size.
     * @param maxEntries
     */
    public MaxEntryLinkedHashMap(Integer maxEntries) {
        super();
        this.maxEntries = maxEntries;
    }

    @Override
    /**
     * Called automatically everything a put() or putAll() is used. If returns true, the eldest
     * entry is removed.
     */
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return (maxEntries != null && size() > maxEntries);
    }
}
