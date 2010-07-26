package com.cannontech.common.events.model;

public class EventLogFilter {
    private String key;
    private ArgumentColumn argumentColumn;
    private EventLogColumnTypeEnum eventLogColumnType;
    private FilterValue filterValue;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArgumentColumn getArgumentColumn() {
        return argumentColumn;
    }

    public void setArgumentColumn(ArgumentColumn argumentColumn) {
        this.argumentColumn = argumentColumn;
    }

    public EventLogColumnTypeEnum getEventLogColumnType() {
        return eventLogColumnType;
    }

    public void setEventLogColumnType(EventLogColumnTypeEnum eventLogColumnType) {
        this.eventLogColumnType = eventLogColumnType;
    }

    public FilterValue getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(FilterValue filterValue) {
        this.filterValue = filterValue;
    }

    public String getFieldName() {
        return argumentColumn.columnName;
    }

}