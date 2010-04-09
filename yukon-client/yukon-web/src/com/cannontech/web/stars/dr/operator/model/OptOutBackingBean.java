package com.cannontech.web.stars.dr.operator.model;

import org.joda.time.LocalDate;

public class OptOutBackingBean {

    private LocalDate startDate;
    private int durationInDays;
    
    
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public int getDurationInDays() {
        return durationInDays;
    }
    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

}