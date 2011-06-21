package com.cannontech.common.bulk.model;

import org.joda.time.Instant;


public class PixelData {
    private boolean hasTransition = false;
    private ReadType readType;
    private Instant start;
    private Instant end;

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

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }
}