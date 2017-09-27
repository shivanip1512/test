package com.cannontech.common.smartNotification.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventData;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.util.Range;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public interface SmartNotificationEventDao {
        
    public enum SortBy {
        DEVICE("Device"),
        TYPE("Type"),
        STATUS("Status"),
        TIMESTAMP("Timestamp"),
        ;
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
        
    }
            
    /**
     * Insert events into the database.
     */
    void save(SmartNotificationEventType type, List<SmartNotificationEvent> event);
    
    /**
     * Returns events for type that have not been processed.
     */
    List<SmartNotificationEvent> getUnprocessedEvents(SmartNotificationEventType type);

    /**
     * Deletes all events. Used by simulator.
     */
    void deleteAllEvents();

    /**
     * Marks events as processed.
     */
    void markEventsAsProcessed(List<SmartNotificationEvent> events, Instant processedTime,
            SmartNotificationFrequency... frequency);

    int getInfrastructureWarningEventDetailCount(DateTime from, DateTime to,
                                                 List<PaoType> typeFilter);

    SearchResults<SmartNotificationEventData> getInfrastructureWarningEventData(DateTimeZone timeZone, 
                                                                                PagingParameters paging, 
                                                                                SortBy sortBy, 
                                                                                Direction direction, 
                                                                                Range<DateTime> dateRange,
                                                                                List<PaoType> typeFilter);

    int getDeviceDataMonitorEventDetailCount(DateTime from, DateTime to, int monitorId);

    SearchResults<SmartNotificationEventData> getDeviceDataMonitorEventData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, Range<DateTime> dateRange,
                                                                            int monitorId); 
}
