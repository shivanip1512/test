package com.cannontech.dr.rfn.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.model.UnknownDevices;

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
     * @return list of all devices with status for rf broadcast message with id {@code messageId}.
     */
    List<PaoIdentifier> getAllDevicesWithStatus(long messageId, PerformanceVerificationMessageStatus status);
    
    /**
     * @return list sorted by PaoName of devices withs tatus for rf broadcast message with id {@code messageId}.
     */
    List<PaoIdentifier> getDevicesWithStatus(long messageId, PerformanceVerificationMessageStatus status, PagingParameters pagingParameters);

    /**
     * @return list sorted by PaoName of devices which have a status of 'UNKNOWN' for rf broadcast message with id {@code messageId}.
     */
    UnknownDevices getDevicesWithUnknownStatus(long messageId, PagingParameters pagingParameters);
    
    /**
     * @return list of all devices which have a status of 'UNKNOWN' for rf broadcast message with id {@code messageId}.
     */
    UnknownDevices getAllDevicesWithUnknownStatus(long messageId);
    
    int getNumberOfDevices(long messageId, PerformanceVerificationMessageStatus status);
    
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
     * Returns the event time for an event (messageId)
     */
    Instant getEventTime(long messageId);
    
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

}