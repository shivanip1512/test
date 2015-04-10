package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RecentResultsCache<T extends Completable> {
    
    private Map<String, Wrapper<T>> completed = new LinkedHashMap<>();
    private Map<String, Wrapper<T>> pending = new LinkedHashMap<>();
    private Set<String> expiredKeys = new HashSet<>();
    
    // 10080 = 7 days
    private int minimumHoldMinutes = 10080;
    
    private Function<Wrapper<T>, T> unwrapper = new Function<Wrapper<T>, T>() {
        @Override public T apply(Wrapper<T> from) { return from.object; }
    };
    
    // GET RESULT
    synchronized public T getResult(String id) throws ResultExpiredException {
        
        T result = null;
        
        processLists();
        
        if (this.completed.containsKey(id)) {
            result =  this.completed.get(id).object;
        }
        else if (this.pending.containsKey(id)) {
            result =  this.pending.get(id).object;
        }
        
        // maybe null is a valid result object, only throw error
        // if this key was indeed removed at some point
        if (result == null && expiredKeys.contains(id)) {
            throw new ResultExpiredException(id);
        }
        
        return result;
    }
    
    synchronized public List<T> getCompleted() {
        processLists();
        return returnSafeList(completed);
    }
    
    synchronized public List<T> getPending() {
        processLists();
        return returnSafeList(pending);
    }
    
    synchronized public List<T> getAll() {
        List<T> safePendingList = getPending();
        List<T> safeCompletedList = getCompleted();
        List<T> allList = new ArrayList<>(safePendingList);
        allList.addAll(safeCompletedList);
        return allList;
    }
    
    synchronized public <E extends T> List<E> getAll(Class<E> clazz) {
        return Lists.newArrayList(Iterables.filter(getAll(), clazz));
    }
    
    synchronized public List<String> getCompletedKeys() {
        processLists();
        return new ArrayList<String>(completed.keySet());
    }
    
    /**
     * Sorts the cached completed values with the specified Comparator, 
     * then returns the keys in that order.
     */
    synchronized public List<String> getSortedCompletedKeys(Comparator<T> comparator) {
        return getSortedKeys(true, comparator);
    }
    
    /**
     * Sorts the cached pending values with the specified Comparator, 
     * then returns the keys in that order.
     */
    synchronized public List<String> getSortedPendingKeys(Comparator<T> comparator) {
        return getSortedKeys(false, comparator);
    }
    
    synchronized public List<String> getPendingKeys() {
        processLists();
        return new ArrayList<String>(pending.keySet());
    }
    
    /**
     * Add a result.
     * @return String key The key used to store the result object in the map.
     */
    synchronized public String addResult(T result) {
        String id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        return addResult(id, result);
    }
    
    /**
     * Add a result using your own specified key.
     * @return String key The key used to store the result object in the map.
     */
    synchronized public String addResult(String key, T result) {
        
        Validate.notNull(result);
        
        if (result.isComplete()) {
            completed.put(key, new Wrapper<T>(result));
        } else {
            pending.put(key, new Wrapper<T>(result));
        }
        
        return key;
    }
    
    /** Remove a result by key */
    synchronized public void remove(String key) {
        pending.remove(key);
        completed.remove(key);
        expiredKeys.add(key);
    }
    
    // HELPERS
    synchronized private void processLists() {
        
        // look for entries that should be completed
        Set<String> pendingKeys = pending.keySet();
        List<String> removePendingKeys = new ArrayList<String>();
        for (String key : pendingKeys) {
            
            Wrapper<T> next = pending.get(key);
            
            if (next.object.isComplete()) {
                removePendingKeys.add(key);
                completed.put(key, next);
            }
        }
        for (String key : removePendingKeys) {
            pending.remove(key);
        }
        
        // look for entries that should be tossed
        Set<String> completedKeys = completed.keySet();
        List<String> removeCompletedKeys = new ArrayList<String>();
        for (String key : completedKeys) {
            
            Wrapper<T> next = completed.get(key);
            if (shouldCompletedBeRemoved(next)) {
                removeCompletedKeys.add(key);
            }
        }
        for (String key : removeCompletedKeys) {
            completed.remove(key);
            expiredKeys.add(key);
        }
    }
    
    /** Gets the completed or pending map. */
    private Map<String, Wrapper<T>> getMap(boolean complete) {
        if (complete) {
            return completed;
        } 
        return pending;
    }
    
    /**
     * Gets the completed or pending results map, with keys and values inverted, 
     * and raw values (stripped of their wrapper).
     */
    private Map<T, String> getInvertedUnwrapped(boolean complete) {
        
        BiMap<String, T> unwrapped = HashBiMap.create();
        for (Map.Entry<String, Wrapper<T>> entry : getMap(complete).entrySet()) {
            unwrapped.put(entry.getKey(), entry.getValue().object);
        }
        
        return unwrapped.inverse();
    }
    
    /**
     * Gets the completed or pending result values, ordered by the specified Comparator.
     */
    private List<T> getSortedValues(boolean completed, Comparator<T> comparator) {
        
        List<T> unsortedList = completed ? getCompleted() : getPending();
        List<T> sortedList = new ArrayList<>(unsortedList);
        Collections.sort(sortedList, comparator);
        
        return sortedList;
    }
    
    /**
     * Sorts the cached complete or pending values with the specified Comparator, 
     * then returns the keys in that order.
     */
    private List<String> getSortedKeys(boolean completed, Comparator<T> comparator) {
        
        processLists();
        
        Map<T, String> invertedUnwrappedMap = getInvertedUnwrapped(completed);
        List<T> sortedValues = getSortedValues(completed, comparator);
        
        List<String> sortedKeys = new ArrayList<>();
        for (T value : sortedValues) {
            String key = invertedUnwrappedMap.get(value);
            sortedKeys.add(key);
        }
        
        return sortedKeys;
    }
    
    private boolean shouldCompletedBeRemoved(Wrapper<T> next) {
        
        long expirationTime = next.addTime + TimeUnit.MILLISECONDS.convert(minimumHoldMinutes, TimeUnit.MINUTES);
        boolean expired = expirationTime < System.currentTimeMillis();
        
        return expired;
    }
    
    synchronized private List<T> returnSafeList(Map<String, Wrapper<T>> results) {
        return Lists.transform(new ArrayList<Wrapper<T>>(results.values()), unwrapper);
    }
    
    /**
     * Wrapper class to track time object has been held so it can be 
     * released (lazily via get lists) when it expires.
     * @param <T> The result type being wrapped
     */
    private static class Wrapper<T> {
        
        public Wrapper(T object) {
            this.object = object;
        }
        
        private T object;
        private long addTime = System.currentTimeMillis();
    }
    
    /** Returns a list of all tasks */
    public Map<String, T> getTasks() {
        
        Map<String, T> all = new HashMap<>();
        for (String key : pending.keySet()) all.put(key, pending.get(key).object);
        for (String key : completed.keySet()) all.put(key, completed.get(key).object);
        
        return all;
    }
    
}