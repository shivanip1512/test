package com.cannontech.stars.xml.serialize;

import org.joda.time.Instant;
import org.joda.time.Duration;

public class ControlHistory {
    private Instant startInstant;
    private Duration controlDuration;
    private boolean hasControlDuration;
    private boolean currentlyControlling = true;

    public ControlHistory() {

    }

    public Instant getStartInstant() {
        return startInstant;
    }
    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }
    
    public Duration getControlDuration() {
        return controlDuration;
    }
    public void setControlDuration(Duration controlDuration) {
        this.controlDuration = controlDuration;
    }

    public boolean isHasControlDuration() {
        return hasControlDuration;
    }
    public void setHasControlDuration(boolean hasControlDuration) {
        this.hasControlDuration = hasControlDuration;
    }

    public boolean isCurrentlyControlling() {
        return currentlyControlling;
    }
    public void setCurrentlyControlling(boolean currentlyControlling) {
        this.currentlyControlling = currentlyControlling;
    }

}
