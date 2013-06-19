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

    /**
     * @param eventCategory
     * @param startDate         Greater-than or equal to this (eg. inclusive).  Remember that it is TIME SENSITIVE
     * @param stopDate          Less than (eg. not inclusive).  Remember that it is TIME SENSITIVE
     * @return
     */
    public List<EventLog> findAllByCategories(Iterable<EventCategory> eventCategory, 
                                              ReadableInstant startDate, 
                                              ReadableInstant stopDate);

    public Set<EventCategory> getAllCategories();
    
    /**
     * This method gets all the event logs between the start and stop date for the given set
     * of event categories.
     * 
     * @param startDate         Greater-than or equal to this (eg. inclusive).  Remember that it is TIME SENSITIVE
     * @param stopDate          Less than (eg. not inclusive).  Remember that it is TIME SENSITIVE
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
     * 
     * @param startDate         Greater-than or equal to this (eg. inclusive).  Remember that it is TIME SENSITIVE
     * @param stopDate          Less than (eg. not inclusive).  Remember that it is TIME SENSITIVE
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
     * 
     * @param startDate         Greater-than or equal to this (eg. inclusive).  Remember that it is TIME SENSITIVE
     * @param stopDate          Less than (eg. not inclusive).  Remember that it is TIME SENSITIVE
     */
    public SearchResult<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                         ReadableInstant startDate,
                                                         ReadableInstant stopDate,
                                                         Integer start,
                                                         Integer pageCount,
                                                         String filterString);
    
    public RowMapperWithBaseQuery<EventLog> getEventLogRowMapper();

    /**
     * 
     * @param searchString
     * @param firstRowIndex     Zero-based row index.  Typically zero or multiples of pageRowCount.
     * @param pageRowCount      Maximum number of rows to return.
     * @return
     */
    public SearchResult<EventLog> findEventsByStringAndPaginate(String searchString, Integer firstRowIndex, Integer pageRowCount);

}
