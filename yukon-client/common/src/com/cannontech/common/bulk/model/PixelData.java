package com.cannontech.common.bulk.model;


public class PixelData {
    private boolean hasTransition = false;
    private ReadType readType;
    private long start;
    private long end;

    public void setReadType(ReadType readType) {
        this.readType = readType;
    }

    public ReadType getReadType() {
        return readType;
    }

    public void setHasTransition(boolean hasTransition) {
        this.hasTransition = hasTransition;
    }

    public boolean isHasTransition() {
        return hasTransition;
    }

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
    
}