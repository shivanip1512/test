package com.cannontech.stars.dr.controlhistory.model;

import org.joda.time.ReadableDuration;
import org.joda.time.DateTime;

public class ControlHistoryEvent {
    private DateTime startDate;
    private DateTime endDate;
    private ReadableDuration duration;
    
    public ControlHistoryEvent() {
        
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * @return - duration is held in seconds
     */
    public ReadableDuration getDuration() {
        return duration;
    }

    public void setDuration(ReadableDuration duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result
                 + ((startDate == null) ? 0 : startDate.hashCode());
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
        ControlHistoryEvent other = (ControlHistoryEvent) obj;
        if (duration == null) {
            if (other.duration != null)
                return false;
        } else if (!duration.equals(other.duration))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }
}
