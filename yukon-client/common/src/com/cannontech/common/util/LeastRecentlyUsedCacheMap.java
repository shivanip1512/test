package com.cannontech.common.util;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ForwardingMap;

public class LeastRecentlyUsedCacheMap<K,V> extends ForwardingMap<K, V> {
    
    private Map<K,V> delegate;
    
    public LeastRecentlyUsedCacheMap(int size) {
        MaxEntryLinkedHashMap<K,V> maxEntryLinkedHashMap = new MaxEntryLinkedHashMap<K, V>(size);
        delegate = Collections.synchronizedMap(maxEntryLinkedHashMap);
    }

    @Override
    protected Map<K, V> delegate() {
        return delegate;
    }

}
