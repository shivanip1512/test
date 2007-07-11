package com.cannontech.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

@SuppressWarnings("unchecked")
public class ExpireLRUMap<E, T extends ExpireLRUMap.ReadDate> {
    private final Map map = Collections.synchronizedMap(new LRUMap());
    
    public T put(final E key, final T value) {
        return (T) map.put(key, value);
    }
    
    public T remove(final E key) {
        return (T) map.remove(key);
    }
    
    public T get(final E key) {
        return get(key, 1);
    }
    
    public T get(final E key, final int days) {
        T t = (T) map.get(key);
        if (t != null && !isExpired(t, days)) {
            return t;
        }
        map.remove(key);
        return null;
    }
    
    public boolean containsKey(final E key) {
        return containsKey(key, 1);
    }
    
    public boolean containsKey(final E key, final int days) {
        boolean parentContainsKey = map.containsKey(key);
        if (parentContainsKey) {
            T t = this.get(key, days);
            return (t != null);
        }
        return false;    
    }
    
    public boolean containsValue(T value) {
        return containsValue(value, 1);
    }
    
    public boolean containsValue(T value, int days) {
        for (final Object obj : map.keySet()) {
            E key = (E) obj;
            if (map.get(key).equals(value)) {
                if (!isExpired(value, days)) {
                    return true;
                }
                map.remove(key);
                break;
            }
        }
        return false;
    }
    
    public boolean isExpired(final T t, final int days) {
        final GregorianCalendar cal = new GregorianCalendar();
        
        cal.setTime(new Date());
        int day = cal.get(Calendar.DAY_OF_YEAR);
        
        cal.setTime(t.getReadDate());
        int readDay = cal.get(Calendar.DAY_OF_YEAR);

        return ((day - readDay) >= days);
    }
    
    public interface ReadDate {
        public Date getReadDate();
    }
}
