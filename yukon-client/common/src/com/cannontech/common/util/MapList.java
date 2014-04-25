package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

/**
 * A utility class that acts like a Map of Lists (Map<A,List<B>>). It is
 * easier to use because the lists are automatically created and removed
 * from the map as needed. The semantics of the various methods follow
 * the List and Map semantics as appropriate
 * 
 * @deprecated Use Guava's {@link Multimap}
 */
@Deprecated
public class MapList<A, B> {
    private Map<A, List<B>> store = new HashMap<A, List<B>>();

    /**
     * Adds value to the end of the list identified by key.
     */
    public synchronized boolean add(A key, B value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return getOrCreate(key).add(value);
    }

    public synchronized boolean addAll(A key, Collection<? extends B> values) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return getOrCreate(key).addAll(values);
    }

    private List<B> getOrCreate(A key) {
        List<B> mySet = store.get(key);
        if (mySet == null) {
            mySet = new LinkedList<B>();
            store.put(key, mySet);
        }
        return mySet;
    }

    /**
     * Tests if the MapQueue contains a queue identified by key that
     * has at least one item in it.
     */
    public synchronized boolean containsKey(A key) {
        List<B> mySet = store.get(key);
        if (mySet != null) {
            return !mySet.isEmpty();
        }
        return false;
    }

    /**
     * Tests if the queue identified by key contains value.
     */
    public synchronized boolean containsValue(A key, B value) {
        List<B> q = store.get(key);
        if (q != null) {
            return q.contains(value);
        }
        return false;
    }

    /**
     * Get the entire contents of the queue identified by key as a new (snapshot) unmodifiable List.
     */
    public List<B> get(A key) {
        List<B> ll = store.get(key);
        if (ll == null || ll.size() == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<B>(ll));
    }

    /**
     * Removes the item equal to value from the queue identified by key. Returns true
     * if an item was successfully removed.
     */
    public synchronized boolean removeValue(A key, B value) {
        List<B> ll = store.get(key);
        if (ll != null) {
            boolean result = ll.remove(value);
            if (ll.isEmpty()) {
                store.remove(key);
            }
            return result;
        }
        return false;
    }

    /**
     * Returns the number of items in the list identified by key. Returns zero
     * if the list doesn't exist.
     */
    public synchronized int size(A key) {
        List<B> mySet = store.get(key);
        if (mySet != null) {
            return mySet.size();
        }
        return 0;
    }

    /**
     * Returns the total number of lists.
     */
    public int size() {
        return store.size();
    }

    /**
     * Returns the keys of all of the lists in the map that contain at least
     * one item. The returned set is a snapshot and will not be modified.
     */
    public Set<A> keySet() {
        return Collections.unmodifiableSet(new HashSet<A>(store.keySet()));
    }

    public Map<A, List<B>> values() {
        ObjectMapper<List<B>, List<B>> mapper = new UnmodifiableListCopyMapper<B>();
        Map<A, List<B>> result = new MappingMap<A, List<B>, List<B>>(store, mapper);
        return Collections.unmodifiableMap(result);
    }
}
