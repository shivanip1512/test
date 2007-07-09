package com.cannontech.core.dynamic.impl;

import java.util.Date;

import com.cannontech.core.dynamic.PointValueHolder;

public class SimplePointValue implements PointValueHolder {

    final int id;
    final Date timestamp;
    final int type;
    final double value;

    public SimplePointValue(final int id, final Date timestamp, final int type, final double value) {
        super();
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Date getPointDataTimeStamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

}
