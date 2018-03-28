package com.cannontech.core.dynamic.impl;

import java.util.Date;

import org.springframework.core.style.ToStringCreator;

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
    
    public SimplePointValue(PointValueHolder value) {
        this(value.getId(), value.getPointDataTimeStamp(), value.getType(), value.getValue());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Date getPointDataTimeStamp() {
        return timestamp;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public double getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + type;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimplePointValue other = (SimplePointValue) obj;
        if (id != other.id) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("id", getId());
        tsc.append("timestamp", getPointDataTimeStamp());
        tsc.append("type", getType());
        tsc.append("value", getValue());
        return tsc.toString(); 
    }

}
