package com.cannontech.stars.xml.serialize;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.OpenInterval;

public class ControlHistoryEntry {
    private Instant startInstant;
    private Duration controlDuration;
    private boolean currentlyControlling = false;

    public ControlHistoryEntry() {}

    public ControlHistoryEntry(OpenInterval interval) {
        if (interval.isOpenStart()) {
            throw new IllegalArgumentException();
        }
        
        if (interval.isOpenEnd()) {
            currentlyControlling = true;
        }
        
        startInstant = interval.getStart();
        
        if (currentlyControlling) {
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
        return currentlyControlling;
    } 
    public void setCurrentlyControlling(boolean currentlyControlling) {
        this.currentlyControlling = currentlyControlling;
    }
    
    public OpenInterval getOpenInterval() {
        if (currentlyControlling) {
            return OpenInterval.createOpenEnd(startInstant);
        } else {
            return OpenInterval.createClosed(getStartInstant(), getEndInstant());
        }
    }
    
}
