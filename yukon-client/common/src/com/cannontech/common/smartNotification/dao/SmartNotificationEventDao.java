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
import com.cannontech.common.stars.scheduledDataImport.AssetImportResultType;
import com.cannontech.common.util.Range;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public interface SmartNotificationEventDao {
        
    public enum SortBy {
        DEVICE_NAME("DeviceName"),
        TYPE("DeviceType"),
        STATUS("Status"),
        TIMESTAMP("Timestamp"),
        WARNING_TYPE("WarningType"),
        JOB_NAME("jobName"),
        FILE_SUCCESS_COUNT("successFileCount"),
        FILE_ERROR_COUNT("filesWithError")
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

    List<SmartNotificationEvent> getEventsByTypeAndDate(SmartNotificationEventType eventType, Range<Instant> range);

    /**
     * Returns watchdog event data to be displayed on UI.
     */
    SearchResults<SmartNotificationEventData> getWatchdogWarningEventData(DateTimeZone timeZone, 
                                                                          PagingParameters paging, 
                                                                          SortBy sortBy, 
                                                                          Direction direction, 
                                                                          Range<DateTime> dateRange);
    /**
     * Returns watchdog event count based on selected from and to time range.
     */
    int getWatchdogWarningEventDetailCount(DateTime from, DateTime to);

    /**
     * Returns asset import event data to be displayed on UI.
     */
    SearchResults<SmartNotificationEventData> getAssetImportEventData(DateTimeZone jodaTimeZone,
            PagingParameters paging, SortBy value, Direction direction, Range<DateTime> range,
            AssetImportResultType assetImportResultType);
    
    /**
     * Returns asset import event count based on selected from and to time range.
     */
    int getAssetImportEventCount(DateTime from, DateTime to, AssetImportResultType assetImportResultType);
}
