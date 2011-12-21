package com.cannontech.stars.dr.controlhistory.model;

import java.util.Comparator;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.collect.Ordering;

public class ControlHistoryEvent {
    private Instant startDate;
    private Instant endDate;
    private Duration duration;
    private boolean controlling;
    private String gears;

    public Instant getStartDate() {
        return startDate;
    }
    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    /**
     * @return - duration is held in seconds
     */
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public boolean isControlling() {
        return controlling;
    }
    public void setControlling(boolean controlling) {
        this.controlling = controlling;
    }

    public String getGears() {
        return gears;
    }
    public void setGears(String gears) {
        this.gears = gears;
    }

    public static Comparator<ControlHistoryEvent> getStartDateComparator() {
        return new Ordering<ControlHistoryEvent>() {
            @Override
            public int compare(ControlHistoryEvent che1, ControlHistoryEvent che2) {
                return che1.getStartDate().compareTo(che2.getStartDate());
            }};
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