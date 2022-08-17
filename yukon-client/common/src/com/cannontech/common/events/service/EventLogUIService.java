package com.cannontech.common.events.service;

import java.util.List;

import org.joda.time.ReadableInstant;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;

public interface EventLogUIService {

    /**
     * This method converts an event log search result from a byType into a data grid (List<List<String>>),
     * which is used to display the results to the web page and the csv file.
     * 
     */
    public List<List<String>> getDataGridRowByType(SearchResults<EventLog> searchResult, 
                                                   YukonUserContext userContext) ;

    /**
     * This method converts an event log search result from a byCategory page into a data grid (List<List<String>>),
     * which is used to display the results to the web page and the csv file.
     * 
     */
    public List<List<String>> getDataGridRowByCategory(SearchResults<EventLog> searchResult, 
                                                       YukonUserContext userContext) ;
    
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
    public SearchResults<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                         ReadableInstant startDate,
                                                         ReadableInstant stopDate,
                                                         Integer start,
                                                         Integer pageCount,
                                                         String filterText,
                                                         YukonUserContext userContext);

    /**
     * This method returns search results for a given event log type and uses the event log sql
     * filters to narrow down the information returned.  You can set the eventLogSqlFilters to
     * null in this case to return all of the entries for the given event log type.
     * 
     */
    public SearchResults<EventLog> getFilteredPagedSearchResultByType(String eventLogType,
                                                                     ReadableInstant startDate,
                                                                     ReadableInstant stopDate,
                                                                     int startIndex,
                                                                     int itemsPerPage,
                                                                     UiFilter<EventLog> eventLogSqlFilters,
                                                                     YukonUserContext userContext);
    

    
}