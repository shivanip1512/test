package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class RecentResultsCache<T extends Completable> {

    private Map<String, Wrapper<T>> completedMap = new LinkedHashMap<String, Wrapper<T>>();
    private Map<String, Wrapper<T>> pendingMap = new LinkedHashMap<String, Wrapper<T>>();
    private Set<String> expiredKeys = new HashSet<String>();
    
    // 10080 = 7 days
    private int minimumHoldMinutes = 10080;
    
    
    private ObjectMapper<Wrapper<T>, T> mapper = new ObjectMapper<Wrapper<T>, T>() {
        @Override
        public T map(RecentResultsCache.Wrapper<T> from) throws ObjectMappingException {
            return from.object;
        };
    };

    // GET RESULT
    synchronized public T getResult(String id) throws ResultResultExpiredException {
        
        T result = null;
        
        processLists();
        
        if (this.completedMap.containsKey(id)) {
            result =  this.completedMap.get(id).object;
        }
        else if (this.pendingMap.containsKey(id)) {
            result =  this.pendingMap.get(id).object;
        }
        
        // maybe null is a valid result object, only throw error
        // if this key was indeed removed at some point
        if (result == null && expiredKeys.contains(id)) {
            throw new ResultResultExpiredException(id);
        }
        
        return result;
    }
    
    // GET COMPLETED/PENDING LISTS
    synchronized public List<T> getCompleted() {
        
        processLists();
        return returnSafeList(completedMap);
    }

    synchronized public List<T> getPending() {
        
        processLists();
        return returnSafeList(pendingMap);
    }
    
    synchronized public List<String> getCompletedKeys() {
        
        processLists();
        return new ArrayList<String>(completedMap.keySet());
    }
    
    /**
     * Sorts the cached completed values with the specified Comparator, then returns the keys in that order.
     */
    synchronized public List<String> getSortedCompletedKeys(Comparator<T> comparator) {
        return getSortedKeys(true, comparator);
    }
    
    /**
     * Sorts the cached pending values with the specified Comparator, then returns the keys in that order.
     */
    synchronized public List<String> getSortedPendingKeys(Comparator<T> comparator) {
        return getSortedKeys(false, comparator);
    }

    synchronized public List<String> getPendingKeys() {
        
        processLists();
        return new ArrayList<String>(pendingMap.keySet());
    }

    // ADD RESULT
    // ability to specify your own key sometimes nice
    synchronized public String addResult(T result) {
        
        String id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        return addResult(id, result);
    }
    
    synchronized public String addResult(String id, T result) {
        
        Validate.notNull(result);
        
        if (result.isComplete()) {
            completedMap.put(id, new Wrapper<T>(result));
        }
        else {
            pendingMap.put(id, new Wrapper<T>(result));
        }
        
        return id;
    }
    
    // HELPERS
    synchronized private void processLists() {
        
        // look for entries that should be completed
        Set<String> pendingKeys = pendingMap.keySet();
        List<String> removePendingKeys = new ArrayList<String>();
        for (String key : pendingKeys) {
            
            Wrapper<T> next = pendingMap.get(key);
            
            if (next.object.isComplete()) {
                removePendingKeys.add(key);
                completedMap.put(key, next);
            }
        }
        for (String key : removePendingKeys) {
            pendingMap.remove(key);
        }
        
        // look for entries that should be tossed
        Set<String> completedKeys = completedMap.keySet();
        List<String> removeCompletedKeys = new ArrayList<String>();
        for (String key : completedKeys) {
            
            Wrapper<T> next = completedMap.get(key);
            if (shouldCompletedBeRemoved(next)) {
                removeCompletedKeys.add(key);
            }
        }
        for (String key : removeCompletedKeys) {
            completedMap.remove(key);
            expiredKeys.add(key);
        }
    }
    
    /**
     * Gets the completed or pending map.
     */
    private Map<String, Wrapper<T>> getMap(boolean completed) {
        if (completed) {
            return completedMap;
        } 
        return pendingMap;
    }
    
    /**
     * Gets the completed or pending results map, with keys and values inverted, and raw values (stripped of their
     * wrapper).
     */
    private Map<T, String> getInvertedUnwrapped(boolean completed) {
        BiMap<String, T> unwrapped = HashBiMap.create();
        for (Map.Entry<String, Wrapper<T>> entry : getMap(completed).entrySet()) {
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
     * Sorts the cached complete or pending values with the specified Comparator, then returns the keys in that order.
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

    synchronized private List<T> returnSafeList(Map<String, Wrapper<T>> map) {
        
        List<Wrapper<T>> list = new ArrayList<Wrapper<T>>(map.values());
        
        MappingList<Wrapper<T>, T> result = new MappingList<Wrapper<T>, T>(list, mapper);
        return Collections.unmodifiableList(result);
    }

    
    // WRAPPER CLASS
    // tracks time object has been held so it can be released (lazily via get lists) when it expires 
    private static class Wrapper<T> {
        
        public Wrapper(T object) {
            this.object = object;
        }
        
        private T object;
        private long addTime = System.currentTimeMillis();
    }
}
