package com.cannontech.services.calculated;

/**
 * Purposely left mutable for easier altering of previous and next fields.
 */
public class CacheValue {
    
    private final double value;
    private final int interval;
    private boolean previous;
    private boolean next;
    
    private CacheValue (double value, int interval, boolean previous, boolean next) {
        this.value = value; this.interval = interval; this.previous = previous; this.next = next;
    }
    public static CacheValue of(double value, int interval, boolean previous, boolean next) {
        return new CacheValue(value, interval, previous, next);
    }
    public double getValue() {
        return value;
    }
    public int getInterval() {
        return interval;
    }
    public boolean isPrevious() {
        return previous;
    }
    public boolean isNext() {
        return next;
    }
    public void setPrevious(boolean previous) {
        this.previous = previous;
    }
    public void setNext(boolean next) {
        this.next = next;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + interval;
        result = prime * result + (next ? 1231 : 1237);
        result = prime * result + (previous ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CacheValue other = (CacheValue) obj;
        if (interval != other.interval)
            return false;
        if (next != other.next)
            return false;
        if (previous != other.previous)
            return false;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }
}