package com.cannontech.dr.rfn.dao;

import java.util.List;
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
     * @param deviceIds the deviceIds of the devices being sent the message.
     */
    void writeNewVerificationEventForDevices(long messageId, Set<Integer> deviceIds);

    /**
     * Writes unenrolled success messages for a collection of devices.
     * @param messageId the unique messageId of the event
     * @param deviceIds the deviceIds of the devices who have reported back the unique id but who were not
     *      enrolled at the time the event was broadcast.
     */
    void writeUnenrolledEventResultForDevices(long messageId, Set<Integer> deviceIds);

    /**
     * @return list of device ids which have a status of 'UNKNOWN' for rf broadcast message with id {@code messageId}.
     */
    Set<Integer> getDevicesWithUnknownStatus(long messageId);

}
