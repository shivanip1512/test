package com.cannontech.web.common.events.model;

import org.joda.time.LocalDate;

import com.cannontech.common.events.model.EventCategory;

public class EventLogCategoryFilter {
    
    private EventCategory[] categories;
    private String filterValue;
    private LocalDate startDate;
    private LocalDate stopDate;
    
    public EventCategory[] getCategories() {
        return categories;
    }
    public void setCategories(EventCategory[] categories) {
        this.categories = categories;
    }
    
    public String getFilterValue() {
        return filterValue;
    }
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
    
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