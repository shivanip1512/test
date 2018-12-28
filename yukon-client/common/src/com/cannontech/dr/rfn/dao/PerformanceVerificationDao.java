package com.cannontech.dr.rfn.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.model.BroadcastEventDeviceDetails;
import com.cannontech.dr.rfn.model.DeviceStatus;

public interface PerformanceVerificationDao {

    /**
     * @return statistics for messages sent in {@code range}.
     * Statistics returned are per-message sent.
     */
    List<PerformanceVerificationEventMessageStats> getReports(Range<Instant> range);

    /**
     * @return a list of messages sent in {@code range}.
     */
    List<PerformanceVerificationEventMessage> getEventMessages(Range<Instant> range);
    
    /**
     * Creates a new verification event in the database.
     * @return a {@link PerformanceVerificationEventMessage} object containing the message id of the event
     * and the time the event was created.
     */
    PerformanceVerificationEventMessage createVerificationEvent();
    
    /**
     * Writes the performance verification event information for the devices being sent
     * a performance verification message.
     * @param messageId the unique message id of the event
     */
    void writeNewVerificationEventForEnrolledDevices(long messageId);
    
    /**
     * Return list of devices participated in a event and have "Success" and "Failure". 
     * This returns the list based on status passed, paging settings and subgroup passed.  
     */
    List<BroadcastEventDeviceDetails> getFilteredDevicesWithStatus(long eventId, List<PerformanceVerificationMessageStatus> status, PagingParameters pagingParameters, List<DeviceGroup> subGroups);

    /**
     * Return list of devices participated in a event and have Unknown status.
     * This returns the list based on status passed, paging settings and subgroup passed.  
     */
    List<BroadcastEventDeviceDetails> getFilteredDevicesWithUnknownStatus(long eventId, PagingParameters pagingParameters, List<DeviceGroup> subGroups);
    
    /**
     * Return count of filtered devices for status "Success" and "Failure".
     */
    int getFilteredCountForStatus(long eventId, List<PerformanceVerificationMessageStatus> status,
            List<DeviceGroup> subGroups);

    /**
     * Return count of devices for status "Unknown".
     */
    int getFilteredCountForUnknownStatus(long eventId, List<DeviceGroup> subGroups);

    /**
	 *Looks up all the event ids for this device and returns the ones that are valid.
	 */
    List<Long> getValidEventIdsForDevice(int deviceId, Iterable<Long> eventIds);
    
    /**
	 *Looks up all the event ids and returns the ones that are valid.
	 */
    List<Long> getValidEventIds(Iterable<Long> eventIds);

    /**
	 *This method creates unenrolled result entry.
	 */
	void createUnenrolledEventResultStatus(int deviceId, long messageId, Instant receivedTime);
	
	/**
	 * This method marks device as a successful
	 */
	void setEventResultStatusToSuccessful(int deviceId, Map<Long, Instant> verificationMsgs);
	
	/**
	 * This method marks device as failure
	 */
	void setEventResultStatusToFailure(int deviceId, Range<Instant> range);
    
    /**
     * Archive event data older than 180 days from table RfnBroadcastEventDeviceStatus to RfnBroadcastEventSummary 
     * Deletes data from RfnBroadcastEventDeviceStatus that is older than 180 days.
     * 
     * In the RfnBroadcastEventSummary only daily count of status is maintained.  
     */
    void archiveRfnBroadcastEventStatus(DateTime removeAfterDate);
    
    /**
     * Retrieve archive event data from table RfnBroadcastEventSummary.  
     */
    
    List<PerformanceVerificationEventMessageStats> getArchiveReports(Range<Instant> range);

    /**
     * Supports archiveRfnBroadcastEventStatus to update
     * RfnBroadcastEventSummary and deleting data from
     * RfnBroadcastEventDeviceStatus that is older than 180 days
     */
    void archiveRfnBroadcastEvents(Long eventId, DateTime removeBeforeDate);
    
    PerformanceVerificationEventMessage getEventMessagesByEvent(long eventId); 
    
    /**
     * Returns performance verification statistics for a single event
     */
    PerformanceVerificationEventMessageStats getReportForEvent(long eventId);
    
    /**
     * Returns status wise device count for unknown devices
     */
    Map<DeviceStatus, Integer> getUnknownCounts(long eventId);
    
    /**
     * Get all devices with the passed status
     */
    List<BroadcastEventDeviceDetails> getAllDevicesWithStatus(long eventId, List<PerformanceVerificationMessageStatus> statuses);
    
    /**
     * Get all devices with unknown status
     */
    List<BroadcastEventDeviceDetails> getAllDevicesWithUnknownStatus(long eventId);
}