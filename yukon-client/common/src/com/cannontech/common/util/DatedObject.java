package com.cannontech.common.util;

import java.util.Date;

public class DatedObject<T> {
    private final Date date;
    private final T object;

    public DatedObject(T object) {
        this.object = object;
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public T getObject() {
        return object;
    }
}
