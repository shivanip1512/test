package com.cannontech.common.util;

import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingSortedMap;

public final class LazySortedMap<K, V> extends ForwardingSortedMap<K, V> {
    private final Function<K, V> function;
    private final SortedMap<K, V> delegate = new TreeMap<K, V>();
    private final Class<K> keyClass;

    public LazySortedMap(Class<K> keyClass, Class<V> valueClass) {
        this(keyClass, new SimpleSupplier<V>(valueClass));
    }

    public LazySortedMap(Class<K> keyClass, final Supplier<V> supplier) {
        this(keyClass, new Function<K, V>() {
            @Override
            public V apply(K from) {
                return supplier.get();
            };
        });
    }

    public LazySortedMap(Class<K> keyClass, Function<K, V> function) {
        this.function = function;
        this.keyClass = keyClass;
    }

    @Override
    protected SortedMap<K, V> delegate() {
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