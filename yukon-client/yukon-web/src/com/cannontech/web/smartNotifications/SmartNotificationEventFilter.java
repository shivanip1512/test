package com.cannontech.web.smartNotifications;

import org.joda.time.DateTime;

public class SmartNotificationEventFilter {
    
    private DateTime startDate;
    private DateTime endDate;
    
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
    

}
