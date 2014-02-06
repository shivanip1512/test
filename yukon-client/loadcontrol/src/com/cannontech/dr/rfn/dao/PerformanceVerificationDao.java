package com.cannontech.dr.rfn.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;

public interface PerformanceVerificationDao {

    /**
     * @return statistics for messages sent in {@code range}.
     * Statistics returned are per-message sent.
     */
    List<PerformanceVerificationEventMessageStats> getReports(Range<Instant> range);

    /**
     * @return statistics for messages sent in {@code range}. Statistics are averaged
     * for each message accounted for and one statistic is returned.
     * 
     * Use {@link #getReports(Range)} for per-message stats
     */
    PerformanceVerificationEventStats getAverageReport(Range<Instant> range);

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
     * @return list of device ids which have a status of 'UNKNOWN' for rf broadcast message with id {@code messageId}.
     */
    Set<Integer> getDevicesWithUnknownStatus(long messageId);
    
    /**
	 *This method looks up all the event ids for this device and returns the ones that are valid.
	 */
    List<Long> getEventIdsForDevice(int deviceId, List<Long> eventIds);
    
    /**
	 *This method looks up all the and returns the ones that are valid.
	 */
    List<Long> getEventIds(List<Long> eventIds);

    /**
	 *This method creates unenrolled result entry.
	 */
	void createUnenrolledEventResult(int deviceId, long messageId, Instant receivedTime);
	
	/**
	 * This method marks device as a successful
	 */
	void updateSuccessEventResult(int deviceId, Map<Long, Instant> verificationMsgs);
	
	/**
	 * This method marks device as unsuccessful
	 */
	
	void updateUnsuccessEventResult(int deviceId, Range<Instant> range);

}
