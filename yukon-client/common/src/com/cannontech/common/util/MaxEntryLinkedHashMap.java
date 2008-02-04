package com.cannontech.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MaxEntryLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    
    private Integer maxEntries;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    /**
     * LinkedHashMap that will remove eldest entries when size exceeds maxEntries.
     * Use null maxEntries to create a LinkedHashMap of unlimited size.
     * @param maxEntries
     */
    public MaxEntryLinkedHashMap(Integer maxEntries) {
        super();
        this.maxEntries = maxEntries;
    }
    
    public MaxEntryLinkedHashMap(Integer maxEntries, boolean accessOrder) {
        super(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, accessOrder);
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
