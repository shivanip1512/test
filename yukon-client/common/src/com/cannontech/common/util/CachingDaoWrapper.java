package com.cannontech.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.jfree.util.Log;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.LoadingCache;

public class CachingDaoWrapper<T extends Identifiable> implements IdentifiableObjectProvider<T> {
    
    private LoadingCache<Integer, T> cache;
    
    public CachingDaoWrapper(final IdentifiableObjectProvider<T> delegate, T... initialItems) {
        
        cache = new AbstractLoadingCache<Integer, T>() {
            private ConcurrentMap<Integer, T> items = new ConcurrentHashMap<>();
            @Override
            public T get(Integer id) throws ExecutionException {
                T returnValue = items.get(id);
                if (returnValue == null) {
                    returnValue = delegate.getForId(id);
                }
                return returnValue;
            }
            @Override
            public T getIfPresent(Object id) {
                return items.get(id);
            }
        };
        
        for (T item : initialItems) {
            cache.put(item.getId(), item);
        }
    }

    @Override
    public T getForId(Integer id) {
        T result = null;
        try {
            result = cache.get(id);
        } catch (ExecutionException e) {
            Log.error("Error retrieving value from cache. Value will be null.", e);
        }
        return result;
    }

}
