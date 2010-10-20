package com.cannontech.stars.xml.serialize;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.OpenInterval;

public class ControlHistoryEntry {
    private Instant startInstant;
    private Duration controlDuration;
    private boolean isCurrentlyControlling = false;

    public ControlHistoryEntry() {}

    public ControlHistoryEntry(OpenInterval interval) {
        if (interval.isOpenStart()) {
            throw new IllegalArgumentException();
        }
        
        if (interval.isOpenEnd()) {
            isCurrentlyControlling = true;
        }
        
        startInstant = interval.getStart();
        
        if (isCurrentlyControlling) {
            controlDuration = interval.withCurrentEnd().toClosedInterval().toDuration();
        } else {
            controlDuration = interval.toClosedInterval().toDuration();
        }
        
    }
    
    public Instant getStartInstant() {
        return new Instant(startInstant);
    }
    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }
    
    public Instant getEndInstant() {
        return getStartInstant().plus(controlDuration);
    }

    public Duration getControlDuration() {
        return controlDuration;
    }
    public void setControlDuration(Duration controlDuration) {
        this.controlDuration = controlDuration;
    }
    
    public boolean isCurrentlyControlling() {
        return isCurrentlyControlling;
    } 
    public void setIsCurrentlyControlling(boolean isCurrentlyControlling) {
        this.isCurrentlyControlling = isCurrentlyControlling;
    }
    
    public OpenInterval getOpenInterval() {
        if (isCurrentlyControlling) {
            return OpenInterval.createOpenEnd(startInstant);
        } else {
            return OpenInterval.createClosed(getStartInstant(), getEndInstant());
        }
    }
    
}
