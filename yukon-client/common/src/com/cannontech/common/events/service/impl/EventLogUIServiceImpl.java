package com.cannontech.common.events.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.EventLogUIService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class EventLogUIServiceImpl implements EventLogUIService { 

    private DateFormattingService dateFormattingService;
    private EventLogDao eventLogDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private FilterService filterService;
    
    @Override
    public List<List<String>> getDataGridRowByType(SearchResult<EventLog> searchResult, YukonUserContext userContext) {
        
        List<EventLog> resultList = searchResult.getResultList();
        
        List<List<String>> dataGrid = Lists.newArrayList();
        for (EventLog eventLog : resultList) {
            DateFormatEnum dateDisplayFormat = DateFormatEnum.BOTH;
            
            List<String> dataRow = Lists.newArrayList();
            
            dataRow.add(eventLog.getEventType());
            dataRow.add(dateFormattingService.format(eventLog.getDateTime(), dateDisplayFormat, userContext));

            for (Object argument : eventLog.getArguments()) {
                if (argument == null) { 
                	dataRow.add("");
                } else if (argument instanceof Date) {
                    dataRow.add(dateFormattingService.format(argument, dateDisplayFormat, userContext));
                } else {
                    dataRow.add(argument.toString());
                }
            }
            dataGrid.add(dataRow);
        }
        return dataGrid;
    }

    @Override
    public List<List<String>> getDataGridRowByCategory(SearchResult<EventLog> searchResult, YukonUserContext userContext) {
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        
        List<EventLog> resultList = searchResult.getResultList();

        List<List<String>> dataGrid = Lists.newArrayListWithExpectedSize(resultList.size());
        for (EventLog eventLog : resultList) {
            DateFormatEnum dateDisplayFormat = DateFormatEnum.BOTH;
            
            List<String> dataRow = Lists.newArrayListWithExpectedSize(resultList.size()*3);
            
            dataRow.add(eventLog.getEventType());
            dataRow.add(dateFormattingService.format(eventLog.getDateTime(), dateDisplayFormat, userContext));
            
            String eventLogMessage = messageSourceAccessor.getMessage(eventLog.getMessageSourceResolvable());
            dataRow.add(eventLogMessage);
            
            dataGrid.add(dataRow);
        }
        return dataGrid;
    }
    
    @Override
    public SearchResult<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories, 
                                                         ReadableInstant startDate, 
                                                         ReadableInstant stopDate, 
                                                         Integer start, 
                                                         Integer pageCount,
                                                         String filterText,
                                                         YukonUserContext userContext) {
        
        // There was no filter text supplied.  Return the whole list of event logs for the
        // supplied time period time period.
        if (StringUtils.isBlank(filterText)) {
            return eventLogDao.getPagedSearchResultByCategories(eventCategories, startDate, 
                                                                stopDate, start, pageCount);
        }

        SearchResult<EventLog> pagedSearchResultByCategories = 
            eventLogDao.getFilteredPagedSearchResultByCategories(eventCategories, 
                                                                 startDate, 
                                                                 stopDate, 
                                                                 start, 
                                                                 pageCount,
                                                                 filterText);
        
        return pagedSearchResultByCategories;
        
    }
    
    @Override
    public SearchResult<EventLog> getFilteredPagedSearchResultByType(String eventLogType,
                                                                     ReadableInstant startDate,
                                                                     ReadableInstant stopDate,
                                                                     int startIndex,
                                                                     int itemsPerPage,
                                                                     UiFilter<EventLog> eventLogSqlFilters,
                                                                     YukonUserContext userContext) {
        
        // Build up the Event Log Type filter and Date Range Filter from the supplied
        // eventLogType and start and stop dates
        List<UiFilter<EventLog>> filters = Lists.newArrayList();
        filters.add(new EventLogTypeFilter(eventLogType));
        filters.add(new EventLogDateRangeFilter(startDate, stopDate));
        if (eventLogSqlFilters != null) {
            filters.add(eventLogSqlFilters);
        }
        UiFilter<EventLog> filter = UiFilterList.wrap(filters);
        
        // Process search results
        SearchResult<EventLog> searchResult =
            filterService.filter(filter, null, startIndex, 
                                 itemsPerPage, eventLogDao.getEventLogRowMapper());

        return searchResult;
    }
    
    // UI Filters
    private static class EventLogTypeFilter extends SqlFragmentUiFilter<EventLog> {

        String eventLogType;
        
        public EventLogTypeFilter(String eventLogType) {
            this.eventLogType = eventLogType;
        }
        
        @Override
        protected void getSqlFragment(SqlBuilder sql) {
            sql.append("EventType").eq(eventLogType);
        }
    };

    private static class EventLogDateRangeFilter extends SqlFragmentUiFilter<EventLog> {

        private Instant startDate;
        private Instant stopDate;
        
        public EventLogDateRangeFilter(ReadableInstant startDate,
                                       ReadableInstant stopDate) {
            this.startDate = startDate.toInstant();
            this.stopDate = stopDate.toInstant();
        }
        
        @Override
        protected void getSqlFragment(SqlBuilder sql) {
            sql.append("EventTime").gte(startDate).append(" AND ");
            sql.append("EventTime").lte(stopDate);
        }
    };
    
    // Dependency Injection
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }
    
    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

}