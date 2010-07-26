package com.cannontech.common.events.service;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.model.FilterValue;

public interface EventLogFilterFactory {
    
    /**
     * The method returns a filter for the supplied FilterValue
     */
    public UiFilter<EventLog> getFilter(FilterValue filterValue, ArgumentColumn argumentColumn);
}