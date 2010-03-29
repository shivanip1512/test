package com.cannontech.web.stars.dr.operator.model;

import java.util.Date;

public class OptOutBackingBean {

    private Date startDate;
    private int durationInDays;
    
    
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public int getDurationInDays() {
        return durationInDays;
    }
    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

}