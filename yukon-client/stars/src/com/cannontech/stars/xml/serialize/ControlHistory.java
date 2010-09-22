package com.cannontech.stars.xml.serialize;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

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
    
    public Instant getStopInstant(){
        return startInstant.plus(controlDuration);
    }

    public Object getInterval() {
        return new Interval(startInstant, getStopInstant());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((controlDuration == null) ? 0 : controlDuration.hashCode());
        result = prime * result + (currentlyControlling ? 1231 : 1237);
        result = prime * result + (hasControlDuration ? 1231 : 1237);
        result = prime * result
                 + ((startInstant == null) ? 0 : startInstant.hashCode());
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
        ControlHistory other = (ControlHistory) obj;
        if (controlDuration == null) {
            if (other.controlDuration != null)
                return false;
        } else if (!controlDuration.equals(other.controlDuration))
            return false;
        if (currentlyControlling != other.currentlyControlling)
            return false;
        if (hasControlDuration != other.hasControlDuration)
            return false;
        if (startInstant == null) {
            if (other.startInstant != null)
                return false;
        } else if (!startInstant.equals(other.startInstant))
            return false;
        return true;
    }

}
