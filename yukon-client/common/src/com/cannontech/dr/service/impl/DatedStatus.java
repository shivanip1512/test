package com.cannontech.dr.service.impl;

import org.joda.time.DateTime;

public abstract class DatedStatus {

    protected final DateTime date;

    public DatedStatus(DateTime date) {
        this.date = date;
    }

    public DateTime getDate() {
        return date;
    }

    public abstract boolean isActive();
}