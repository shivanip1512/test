package com.cannontech.common.util;

import java.util.Date;

public class DatedObject<T> {
    private Date date;
    private T object;

    public DatedObject(Date date, T object) {
        this.date = date;
        this.object = object;
    }

    public DatedObject(T object) {
        this(new Date(), object);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public void touch() {
        date = new Date();
    }
}
