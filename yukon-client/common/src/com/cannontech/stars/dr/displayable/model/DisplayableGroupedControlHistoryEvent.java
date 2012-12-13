package com.cannontech.stars.dr.displayable.model;

import java.util.SortedSet;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.stars.dr.controlHistory.model.ControlHistoryEvent;
import com.google.common.collect.Ordering;

public class DisplayableGroupedControlHistoryEvent {
   
    private Instant startDate;
    private Instant endDate;
    private Duration duration;
    private boolean controlling;

    private SortedSet<ControlHistoryEvent> controlHistory;
    
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

    public static Ordering<DisplayableGroupedControlHistoryEvent> getStartDateComparator() {
        return new Ordering<DisplayableGroupedControlHistoryEvent>() {
            @Override
            public int compare(DisplayableGroupedControlHistoryEvent che1, DisplayableGroupedControlHistoryEvent che2) {
                return che1.getStartDate().compareTo(che2.getStartDate());
            }};
    }
    
    public SortedSet<ControlHistoryEvent> getControlHistory() {
        return controlHistory;
    }
    public void setControlHistory(SortedSet<ControlHistoryEvent> controlHistory) {
        this.controlHistory = controlHistory;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((controlHistory == null) ? 0 : controlHistory.hashCode());
        result = prime * result + (controlling ? 1231 : 1237);
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
        DisplayableGroupedControlHistoryEvent other = (DisplayableGroupedControlHistoryEvent) obj;
        if (controlHistory == null) {
            if (other.controlHistory != null)
                return false;
        } else if (!controlHistory.equals(other.controlHistory))
            return false;
        if (controlling != other.controlling)
            return false;
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
