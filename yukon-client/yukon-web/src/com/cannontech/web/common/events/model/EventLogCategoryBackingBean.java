package com.cannontech.web.common.events.model;

import org.joda.time.LocalDate;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.util.CtiUtilities;

public class EventLogCategoryBackingBean {
    private EventCategory[] categories;
    private String filterValue;
    private LocalDate startDate;
    private LocalDate stopDate;
    private int itemsPerPage = 50;
    private int page = 1;
    
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

    public int getItemsPerPage() {
        return itemsPerPage;
    }
    public void setItemsPerPage(int itemsPerPage) {
        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE) {
            // Restrict the maximum value (yuk-11779)
            this.itemsPerPage = CtiUtilities.MAX_ITEMS_PER_PAGE;
        } else {
            this.itemsPerPage = itemsPerPage;
        }
    }
    
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getStartIndex() {
        return (page - 1) * itemsPerPage;
    }
}