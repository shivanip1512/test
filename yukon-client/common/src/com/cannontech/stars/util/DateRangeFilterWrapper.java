package com.cannontech.stars.util;

import java.util.Date;


public class DateRangeFilterWrapper extends FilterWrapper {
    private Date startDate;
    private Date stopDate;
    
    public DateRangeFilterWrapper(FilterWrapper filterWrapper) {
        super(filterWrapper.getFilterTypeID(), filterWrapper.getFilterText(), filterWrapper.getFilterID());
    }
    
    public DateRangeFilterWrapper(String typeID, String newText, String selectID) {
        super(typeID, newText, selectID);
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }
    
    public Date getStopDate() {
        return stopDate;
    }
    
}
