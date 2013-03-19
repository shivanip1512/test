package com.cannontech.services.calculated;

public final class CacheKey {
    
    private final int pointId;
    private final long timestamp;
    
    private CacheKey(int pointId, long timestamp) {
        this.pointId = pointId;
        this.timestamp = timestamp;
    }
    public static CacheKey of(int pointId, long timestamp) {
        return new CacheKey(pointId, timestamp);
    }
    public int getPointId() {
        return pointId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pointId;
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
        CacheKey other = (CacheKey) obj;
        if (pointId != other.pointId)
            return false;
        if (timestamp != other.timestamp)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("CacheKey [pointId=%s, timestamp=%s]", pointId, timestamp);
    }
    
}