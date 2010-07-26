package com.cannontech.common.events.model;

import org.joda.time.DateTime;

public class DateFilterValue extends FilterValue {
    DateTime startDate;
    DateTime stopDate;
    
    public DateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getStopDate() {
        return stopDate;
    }
    public void setStopDate(DateTime stopDate) {
        this.stopDate = stopDate;
    }
    
}