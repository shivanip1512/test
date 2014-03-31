package com.cannontech.common.util;

import org.joda.time.Instant;

public final class DatedObject<T> {
    private final Instant date;
    private final T object;

    public DatedObject(T object) {
        this.object = object;
        this.date = new Instant();
    }

    public Instant getDate() {
        return date;
    }

    public T getObject() {
        return object;
    }
}
