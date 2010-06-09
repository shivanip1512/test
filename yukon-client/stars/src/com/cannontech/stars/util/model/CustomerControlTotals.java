package com.cannontech.stars.util.model;

import org.joda.time.Duration;

public class CustomerControlTotals {
    Duration totalControlTime = Duration.ZERO;
    Duration totalOptOutTime = Duration.ZERO;
    Duration totalControlDuringOptOutTime = Duration.ZERO;
    int totalOptOutEvents = 0;
    
    public Duration getTotalControlTime() {
        return totalControlTime;
    }
    public void setTotalControlTime(Duration totalControlTime) {
        this.totalControlTime = totalControlTime;
    }
    
    public Duration getTotalOptOutTime() {
        return totalOptOutTime;
    }
    public void setTotalOptOutTime(Duration totalOptOutTime) {
        this.totalOptOutTime = totalOptOutTime;
    }

    public Duration getTotalControlDuringOptOutTime() {
        return totalControlDuringOptOutTime;
    }
    public void setTotalControlDuringOptOutTime(Duration totalControlDuringOptOutTime) {
        this.totalControlDuringOptOutTime = totalControlDuringOptOutTime;
    }
    
    public int getTotalOptOutEvents() {
        return totalOptOutEvents;
    }
    public void setTotalOptOutEvents(int totalOptOutEvents) {
        this.totalOptOutEvents = totalOptOutEvents;
    }

}