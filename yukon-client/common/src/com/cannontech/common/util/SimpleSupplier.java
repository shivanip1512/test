package com.cannontech.common.util;

import com.google.common.base.Supplier;

final class SimpleSupplier<T> implements Supplier<T> {
    private final Class<T> classToSupply;
    public SimpleSupplier(Class<T> classToSupply) {
        this.classToSupply = classToSupply;
    }

    @Override
    public T get() {
        try {
            return classToSupply.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}