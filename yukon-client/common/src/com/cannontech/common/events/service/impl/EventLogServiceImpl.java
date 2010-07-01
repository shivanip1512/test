package com.cannontech.common.events.service.impl;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.EventLogService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;

@Service
public class EventLogServiceImpl implements EventLogService{
 
    private DateFormattingService dateFormattingService;
    private EventLogDao eventLogDao;
    
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

        // Check to see if the filter text can be used as a number.
        Double filterDouble = null;
        try {
            filterDouble = Double.parseDouble(filterText);
        } catch (NumberFormatException e) {
            // This is fine.  It just means the filter text cannot be used a numeric value.
        }
        
        // Check to see if the filter text can be used as a date
        Instant filterInstant = null;
        try {
            filterInstant = 
                new Instant(dateFormattingService.flexibleDateParser(filterText, userContext));
        } catch (ParseException e) {
            // This is fine.  It just means the filter text cannot be used as a date.
        }
        
        SearchResult<EventLog> pagedSearchResultByCategories = 
            eventLogDao.getFilteredPagedSearchResultByCategories(eventCategories, 
                                                                 startDate, 
                                                                 stopDate, 
                                                                 start, 
                                                                 pageCount,
                                                                 filterText,
                                                                 filterDouble,
                                                                 filterInstant);
        
        return pagedSearchResultByCategories;
        
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }

}