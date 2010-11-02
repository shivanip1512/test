package com.cannontech.stars.util.model;

import org.joda.time.Duration;

public class CustomerControlTotals {
    private Duration totalControlTime = Duration.ZERO;
    private Duration totalOptOutTime = Duration.ZERO;
    private Duration totalControlDuringOptOutTime = Duration.ZERO;
    private int totalOptOutEvents = 0;
    
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
    
    public CustomerControlTotals plus(CustomerControlTotals customerControlTotals) {
        CustomerControlTotals result = new CustomerControlTotals();
        
        Duration controlDuringOptOutTime = 
            this.totalControlDuringOptOutTime.plus(customerControlTotals.getTotalControlDuringOptOutTime());
        result.setTotalControlDuringOptOutTime(controlDuringOptOutTime);
        
        Duration controlTime = this.totalControlTime.plus(customerControlTotals.getTotalControlTime());
        result.setTotalControlTime(controlTime);
        
        Duration optOutTime = this.totalOptOutTime.plus(customerControlTotals.getTotalOptOutTime());
        result.setTotalOptOutTime(optOutTime);
        
        int optOutEvents = this.totalOptOutEvents + customerControlTotals.getTotalOptOutEvents();
        result.setTotalOptOutEvents(optOutEvents);
        
        return result;
    }

}