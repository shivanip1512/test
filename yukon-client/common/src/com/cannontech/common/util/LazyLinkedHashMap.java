package com.cannontech.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMap;

public class LazyLinkedHashMap<K, V> extends ForwardingMap<K, V> {
    
    private final Function<K, V> function;
    private final LinkedHashMap<K, V> delegate = new LinkedHashMap<K, V>();

    public LazyLinkedHashMap(Class<V> clazz) {
        this(new SimpleSupplier<V>(clazz));
    }
    
    public LazyLinkedHashMap(final Supplier<V> supplier) {
        this(new Function<K, V>() {
            public V apply(K from) {
                return supplier.get();
            };
        });
    }
    
    public LazyLinkedHashMap(Function<K,V> function) {
        this.function = function;
    }
    
    @Override
    protected Map<K, V> delegate() {
        return delegate;
    }
    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    };
    
    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        if (delegate.containsKey(key)) {
            return delegate.get(key);
        }
        V value = function.apply((K) key);
        delegate.put((K)key, value);
        return value;
    }

}
