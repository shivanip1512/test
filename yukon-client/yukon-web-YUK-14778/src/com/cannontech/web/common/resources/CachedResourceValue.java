package com.cannontech.web.common.resources;

public final class CachedResourceValue {

    private String value;
    private long timestamp;
    
    private CachedResourceValue(String value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }
    
    public static CachedResourceValue of(String value, long timestamp) {
        return new CachedResourceValue(value, timestamp);
    }
    
    public String getValue() {
        return value;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
}