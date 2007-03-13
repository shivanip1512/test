package com.cannontech.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * A utility class that acts like a Map of Queues (Map<A,Queue<B>>). It is 
 * easier to use because the queues are automatically created and removed
 * from the map as needed. The semantics of the various methods follow
 * the Queue and Map semantics as appropriate
 */
public class MapQueue<A,B> {
    private Map<A, LinkedList<B>> store = new HashMap<A, LinkedList<B>>();

    
    /**
     * Adds value to the end of the queue identified by key. This will 
     * only return false when value is null. In all other cases the queue
     * is unbounded and will accept as many values as the underlying 
     * data structure allows.
     * @param key
     * @param value
     * @return
     */
    public boolean offer(A key, B value) {
        if (value == null) return false;
        if (key == null) throw new IllegalArgumentException();
        return getOrCreate(key).offer(value);
    }
    
    /**
     * Alias for offer(key,value).
     */
    public boolean add(A key, B value) {
        return offer(key,value);
    }
    
    private LinkedList<B> getOrCreate(A key) {
        LinkedList<B> mySet = store.get(key);
        if (mySet == null) {
            mySet = new LinkedList<B>();
            store.put(key, mySet);
        }
        return mySet;
    }
    
    /**
     * Tests if the MapQueue contains a queue identified by key that
     * has at least one item in it.
     * @param key
     * @return
     */
    public boolean containsKey(A key) {
        Queue<B> mySet = store.get(key);
        if (mySet != null) {
            return !mySet.isEmpty();
        } else {
            return false;
        }
    }
    
    /**
     * Tests if the queue identified by key contains value.
     * @param key
     * @param value
     * @return
     */
    public boolean containsValue(A key, B value) {
        Queue<B> q = store.get(key);
        if (q != null) {
            return q.contains(value);
        } else {
            return false;
        }
    }
    
    /**
     * Get the entire contents of the queue identified by key as an ordered unmodifiable List.
     * @param key
     * @return
     */
    public List<B> get(A key) {
        LinkedList<B> ll = store.get(key);
        if (ll == null || ll.size() == 0) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(ll);
        }
    }
    
    /**
     * Removes the first item from the queue identified by key.
     * @param key
     * @return
     * @throws NoSuchElementException if the queue is empty
     */
    public B remove(A key) {
        LinkedList<B> q = store.get(key);
        if (q != null) {
            B result = q.remove();
            if (q.isEmpty()) {
                store.remove(key);
            }
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }
    
    /**
     * Removes and returns the entire queue identified by key. Never returns null.
     * 
     * @param key
     * @return
     */
    public Queue<B> removeKey(A key) {
        LinkedList<B> remove = store.remove(key);
        if (remove == null) {
            return new LinkedList<B>();
        } else {
            return remove;
        }
    }
    
    /**
     * Removes the item equal to value from the queue identified by key. Returns true
     * if an item was succesfully removed.
     * @param key
     * @param value
     * @return
     */
    public boolean removeValue(A key, B value) {
        LinkedList<B> ll = store.get(key);
        if (ll != null) {
            boolean result = ll.remove(value);
            if (ll.isEmpty()) {
                store.remove(key);
            }
            return result;
        } else {
            return false;
        }
    }
    
    /**
     * Removes and returns the first item in the queue identified by key. Returns null
     * if the queue doesn't exist or the queue is empty.
     * @param key
     * @return
     */
    public B poll(A key) {
        LinkedList<B> q = store.get(key);
        if (q != null) {
            B result = q.remove();
            if (q.isEmpty()) {
                store.remove(key);
            }
            return result;
        } else {
            return null;
        }
    }
    
    /**
     * Returns the number of items in the queue identified by key. Returns zero
     * if the queue doesn't exist.
     * @param key
     * @return
     */
    public int size(A key) {
        LinkedList<B> mySet = store.get(key);
        if (mySet != null) {
            return mySet.size();
        } else {
            return 0;
        }
    }
    
    /**
     * Returns the total number of queues.
     * @return
     */
    public int size() {
        return store.size();
    }
    
    /**
     * Returns the keys of all of the queues in the map that contain at least
     * one item.
     * @return
     */
    public Set<A> keySet() {
        return Collections.unmodifiableSet(store.keySet());
    }
    
}
