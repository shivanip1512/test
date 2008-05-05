package com.cannontech.web.stars.dr.consumer.displayable.model;

import java.util.Date;

public class DisplayableOptOut {

    public enum DisplayableOptOutType {
        FOR,
        FROM
    }
    
    private final Date startDate;
    private final Date endDate;
    private final DisplayableOptOutType type;
    
    public DisplayableOptOut(final Date startDate, final Date endDate, final DisplayableOptOutType type) {
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
        boolean result = type.equals(DisplayableOptOutType.FOR);
        return result;
    }
    
    public boolean isFromDisplay() {
        boolean result = type.equals(DisplayableOptOutType.FROM);
        return result;
    }
    
    public DisplayableOptOutType getType() {
        return type;
    }
    
}
