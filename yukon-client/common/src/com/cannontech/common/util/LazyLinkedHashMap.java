package com.cannontech.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMap;

public class LazyLinkedHashMap<K, V> extends ForwardingMap<K, V> {
    
    private final Function<K, V> function;
    private final LinkedHashMap<K, V> delegate = new LinkedHashMap<K, V>();
    private final Class<K> keyClass;

    public LazyLinkedHashMap(Class<K> keyClass, Class<V> valueClass) {
        this(keyClass, new SimpleSupplier<V>(valueClass));
    }
    
    public LazyLinkedHashMap(Class<K> keyClass, final Supplier<V> supplier) {
        this(keyClass, new Function<K, V>() {
            public V apply(K from) {
                return supplier.get();
            };
        });
    }
    
    public LazyLinkedHashMap(Class<K> keyClass, Function<K,V> function) {
        this.function = function;
        this.keyClass = keyClass;
    }
    
    @Override
    protected Map<K, V> delegate() {
        return delegate;
    }
    
    @Override
    public V get(Object key) {
        K castedKey = keyClass.cast(key);
        if (delegate.containsKey(key)) {
            return delegate.get(key);
        }
        V value = function.apply(castedKey);
        delegate.put(castedKey, value);
        return value;
    }
}