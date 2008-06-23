package com.cannontech.web.stars.dr.consumer.displayable.model;

import java.util.Date;

public class DisplayableScheduledOptOut {

    public enum DisplayableScheduledOptOutType {
        FOR,
        FROM
    }
    
    private final Date startDate;
    private final Date endDate;
    private final DisplayableScheduledOptOutType type;
    
    public DisplayableScheduledOptOut(final Date startDate, final Date endDate, final DisplayableScheduledOptOutType type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public boolean isForDisplay() {
        boolean result = type.equals(DisplayableScheduledOptOutType.FOR);
        return result;
    }
    
    public boolean isFromDisplay() {
        boolean result = type.equals(DisplayableScheduledOptOutType.FROM);
        return result;
    }
    
    public DisplayableScheduledOptOutType getType() {
        return type;
    }
    
}
