package com.cannontech.common.events.service;

import org.joda.time.ReadableInstant;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.search.SearchResult;
import com.cannontech.user.YukonUserContext;

public interface EventLogService {
    
    /**
     * This method gets all the event logs that contain the filter value between the start and 
     * stop date for the given set of event categories.  If you do not supply a filter
     * value, you will be returned the full list of event logs for the given time period and set of
     * event categories.  The filter value will be tried as three more specific database values: 
     * a basic String, a Number, and a Date.
     * 
     * NOTE: The filter value will be treated as case insensitive being dealt with a string.
     *   (user = USER)
     */
    public SearchResult<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                         ReadableInstant startDate,
                                                         ReadableInstant stopDate,
                                                         Integer start,
                                                         Integer pageCount,
                                                         String filterText,
                                                         YukonUserContext userContext);
    
}
