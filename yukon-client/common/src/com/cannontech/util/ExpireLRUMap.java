package com.cannontech.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.time.DateUtils;

@SuppressWarnings("unchecked")
public class ExpireLRUMap<K, V extends ExpireLRUMap.ReadDate> {
    private final Map map;
    private int expireDays = 1;
    
    public ExpireLRUMap() {
        map = Collections.synchronizedMap(new LRUMap());
    }
    
    public ExpireLRUMap(final int size) {
        map = Collections.synchronizedMap(new LRUMap(size));
    }
    
    public V put(final K key, final V value) {
        return (V) map.put(key, value);
    }
    
    public V remove(final K key) {
        return (V) map.remove(key);
    }
    
    public V get(final K key) {
        return get(key, expireDays);
    }
    
    public V get(final K key, final int days) {
        V t = (V) map.get(key);
        if (t != null && !isExpired(t, days)) {
            return t;
        }
        map.remove(key);
        return null;
    }
    
    public boolean containsKey(final K key) {
        return containsKey(key, expireDays);
    }
    
    public boolean containsKey(final K key, final int days) {
        boolean parentContainsKey = map.containsKey(key);
        if (parentContainsKey) {
            V t = this.get(key, days);
            return (t != null);
        }
        return false;    
    }
    
    public boolean containsValue(V value) {
        return containsValue(value, expireDays);
    }
    
    public boolean containsValue(V value, int days) {
        for (final Object obj : this.keySet()) {
            K key = (K) obj;
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
    
    public boolean isExpired(final V t, final int days) {
        final Calendar now = Calendar.getInstance();
        final Calendar readDate = Calendar.getInstance();
        
        readDate.setTime(DateUtils.truncate(t.getReadDate(), Calendar.HOUR));
        readDate.add(Calendar.DAY_OF_YEAR, days);
        
        boolean result = readDate.before(now);
        return result;
    }
    
    public int size() {
        return map.size();
    }
    
    public Collection<V> values() {
        return new ArrayList<V>(map.values());
    }
    
    public Set<K> keySet() {
        return new HashSet<K>(map.keySet());
    }
    
    public void setDefaultExpire(final int expireDays) {
        this.expireDays = expireDays;
    }
    
    public interface ReadDate {
        public Date getReadDate();
    }
}
