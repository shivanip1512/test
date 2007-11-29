package com.cannontech.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapSet<A,B> {
    private Map<A, Set<B>> store = new HashMap<A, Set<B>>();
    
    public boolean add(A key, B value) {
        if (value == null) return false;
        return getAndCreate(key).add(value);
    }
    
    public boolean add(A key, Set<B> values) {
        if (values == null) return false;
        if (values.isEmpty()) return false;
        return getAndCreate(key).addAll(values);
    }
    
    private Set<B> getAndCreate(A key) {
        Set<B> mySet = store.get(key);
        if (mySet == null) {
            mySet = new HashSet<B>();
            store.put(key, mySet);
        }
        return mySet;
    }
    
    public Set<B> get(A key) {
        Set<B> mySet = store.get(key);
        if (mySet == null || mySet.size() == 0) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(mySet);
        }
    }
    
    public Set<A> keySet() {
        return store.keySet();
    }
    
    public boolean containsKey(A key) {
        Set<B> mySet = store.get(key);
        if (mySet != null) {
            return !mySet.isEmpty();
        } else {
            return false;
        }
    }
    
    public boolean containsValue(A key, B value) {
        Set<B> mySet = store.get(key);
        if (mySet != null) {
            return mySet.contains(value);
        } else {
            return false;
        }
    }
    
    public boolean isEmpty() {
        return store.isEmpty();
    }
    
    public Set<B> removeKey(A key) {
        return store.remove(key);
    }
    
    public boolean removeValue(A key, B value) {
        Set<B> mySet = store.get(key);
        if (mySet != null) {
            boolean result = mySet.remove(value);
            if (mySet.isEmpty()) {
                store.remove(key);
            }
            return result;
        } else {
            return false;
        }
    }
    
    public Map<A, Set<B>> getBackingStore() {
        return store;
    }

}
