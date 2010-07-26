package com.cannontech.common.events.dao;

import java.util.List;
import java.util.Set;

import org.joda.time.ReadableInstant;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.search.SearchResult;

public interface EventLogDao {

    public void insert(EventLog eventLog);

    public List<ArgumentColumn> getArgumentColumns();

    public List<EventLog> findAllByCategories(Iterable<EventCategory> eventCategory, 
                                              ReadableInstant startDate, 
                                              ReadableInstant stopDate);

    public Set<EventCategory> getAllCategories();
    
    /**
     * This method gets all the event logs between the start and stop date for the given set
     * of event categories.
     */
    public SearchResult<EventLog> 
                getPagedSearchResultByCategories(Iterable<EventCategory> eventCategories, 
                                                 ReadableInstant startDate, 
                                                 ReadableInstant stopDate, 
                                                 Integer start, 
                                                 Integer pageCount);

    /**
     * This method gets all the event logs between the start and stop date for the supplied
     * event log types.
     */
    public SearchResult<EventLog> 
                getPagedSearchResultByLogTypes(Iterable<String> eventLogTypes, 
                                               ReadableInstant startDate, 
                                               ReadableInstant stopDate, 
                                               Integer start, 
                                               Integer pageCount);

    /**
     * This method gets all the event logs that have any of the filter values between the supplied
     * start and stop date for the given set of event categories. 
     * 
     * NOTE: Any of the filter values maybe set to null.
     */
    public SearchResult<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                         ReadableInstant startDate,
                                                         ReadableInstant stopDate,
                                                         Integer start,
                                                         Integer pageCount,
                                                         String filterString,
                                                         Double filterNumber,
                                                         ReadableInstant filterInstant);
    
    public RowMapperWithBaseQuery<EventLog> getEventLogRowMapper();

}
