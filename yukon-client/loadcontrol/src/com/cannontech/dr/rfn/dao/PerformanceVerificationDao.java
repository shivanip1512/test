package com.cannontech.dr.rfn.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.impl.PerformanceVerificationDaoImpl.UnknownDevices;

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
     * @return list of devices which have a status of 'UNKNOWN' for rf broadcast message with id {@code messageId}.
     */
    UnknownDevices getDevicesWithUnknownStatus(long messageId);
    
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

}
