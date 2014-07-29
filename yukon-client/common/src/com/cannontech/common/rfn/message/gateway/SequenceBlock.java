package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

public class SequenceBlock implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long start;
    private long end;
    
    public long getStart() {
        return start;
    }
    
    public void setStart(long start) {
        this.start = start;
    }
    
    public long getEnd() {
        return end;
    }
    
    public void setEnd(long end) {
        this.end = end;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (end ^ (end >>> 32));
        result = prime * result + (int) (start ^ (start >>> 32));
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
        SequenceBlock other = (SequenceBlock) obj;
        if (end != other.end)
            return false;
        if (start != other.start)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("SequenceBlock [start=%s, end=%s]", start, end);
    }
    
}