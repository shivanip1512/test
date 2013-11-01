package com.cannontech.web.common.events.model;

import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import com.cannontech.common.events.model.EventLogFilter;
import com.cannontech.common.util.CtiUtilities;
import com.google.common.collect.Lists;

public class EventLogTypeBackingBean {
    private String eventLogType;
    private LocalDate startDate;
    private LocalDate stopDate;
    private int itemsPerPage = 50;
    private int page = 1;

    private List<EventLogFilter> eventLogFilters = Lists.newArrayList();
    
    public EventLogTypeBackingBean(){}
    public EventLogTypeBackingBean(String eventLogType,
                                   DateTimeZone timeZone,
                                   List<EventLogFilter> eventLogFilters) {
        this.eventLogType = eventLogType;
        
        this.startDate = new LocalDate(timeZone).minusDays(1);
        this.stopDate = new LocalDate(timeZone);

        this.eventLogFilters = eventLogFilters;
    }
    
    public String getEventLogType() {
        return eventLogType;
    }
    public void setEventLogType(String eventLogType) {
        this.eventLogType = eventLogType;
    }
    
    public List<EventLogFilter> getEventLogFilters() {
        return eventLogFilters;
    }
    public void setEventLogFilters(List<EventLogFilter> eventLogFilters) {
        this.eventLogFilters = eventLogFilters;
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
        this.itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
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