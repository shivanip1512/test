package com.cannontech.common.events.model;

import org.joda.time.LocalDate;

public class DateFilterValue extends FilterValue {
    private LocalDate startDate;
    private LocalDate stopDate;
    
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }
    public void setStopDate(LocalDate stopDate) {
        this.stopDate = stopDate;
    }
    
}