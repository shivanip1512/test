package com.cannontech.web.common.events.model;

import org.joda.time.LocalDate;

public class EventLogCategoryBackingBean {
    private String[] categories;
    private String filterValue;
    private LocalDate startDate;
    private LocalDate stopDate;
    private int itemsPerPage = 10;
    private int page = 1;
    
    public String[] getCategories() {
        return categories;
    }
    public void setCategories(String[] categories) {
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
        this.itemsPerPage = itemsPerPage;
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