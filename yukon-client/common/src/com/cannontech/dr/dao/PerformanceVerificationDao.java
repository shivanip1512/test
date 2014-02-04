package com.cannontech.dr.dao;

import java.util.List;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;

public interface PerformanceVerificationDao {

    /**
     * @Returns statistics for messages sent from {@code stop.minus(duration)} to {@code stop}.
     * Statistics returned are per-message sent.
     */
    List<PerformanceVerificationEventMessageStats> getReports(Range<Instant> range);

    /**
     * @Returns statistics for messages sent from {@code stop.minus(duration)} to {@code stop}. Statistics are averaged
     * for each message accounted for and one statistic is returned.
     * 
     * Use {@link #getReports(Duration, Instant)} for per-message stats
     */
    PerformanceVerificationEventStats getAverageReport(Range<Instant> range);

    /**
     * @Returns a list of messages sent from {@code stop.minus(duration)} to {@code stop}.
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
    public void writeNewVerificationEventForDevices(long messageId, Set<Integer> deviceIds);

    /**
     * Writes unenrolled success messages for a collection of devices.
     * @param messageId the unique messageId of the event
     * @param deviceIds the deviceIds of the devices who have reported back the unique id but who were not
     *      enrolled at the time the event was broadcast.
     */
    public void writeUnenrolledEventResultForDevices(long messageId, Set<Integer> deviceIds);

    List<Integer> getDeviceIdsWithUnknownStatus(Range<Instant> range);
}
