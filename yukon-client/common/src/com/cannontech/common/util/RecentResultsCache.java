package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.cannontech.common.bulk.mapper.ObjectMappingException;

public class RecentResultsCache<T extends Completable> {

    private Map<String, Wrapper<T>> completedMap = new LinkedHashMap<String, Wrapper<T>>();
    private Map<String, Wrapper<T>> pendingMap = new LinkedHashMap<String, Wrapper<T>>();
    
    private int minimumHoldMinutes = 60;
    
    
    private ObjectMapper<Wrapper<T>, T> mapper = new ObjectMapper<Wrapper<T>, T>() {
        public T map(RecentResultsCache.Wrapper<T> from) throws ObjectMappingException {
            return from.object;
        };
    };

    // GET RESULT
    synchronized public T getResult(String id) {
        
        if (this.completedMap.containsKey(id)) {
            return this.completedMap.get(id).object;
        }
        else if (this.pendingMap.containsKey(id)) {
            return this.pendingMap.get(id).object;
        }
        return null;
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
        }
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
