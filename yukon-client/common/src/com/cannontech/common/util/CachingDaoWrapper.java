package com.cannontech.common.util;

import java.util.concurrent.ConcurrentMap;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.core.dao.support.IdAccessible;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class CachingDaoWrapper<T extends Identifiable> implements IdAccessible<T> {
    
    private ConcurrentMap<Integer, T> computingMap;

    public CachingDaoWrapper(final IdAccessible<T> delegate, T... initialItems) {
        
        computingMap = new MapMaker().concurrencyLevel(1).makeComputingMap(new Function<Integer, T>() {
            public T apply(Integer from) {
                return delegate.getForId(from);
            }
        });
        
        for (int i = 0; i < initialItems.length; i++) {
            T t = initialItems[i];
            computingMap.put(t.getId(), t);
        }
    }

    @Override
    public T getForId(Integer id) {
        T result = computingMap.get(id);
        return result;
    }

}
