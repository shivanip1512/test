package com.cannontech.dr.dao;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;

public interface PerformanceVerificationDao {

    /**
     * @Returns statistics for messages sent from {@code stop.minus(duration)} to {@code stop}.
     * Statistics returned are per-message sent.
     */
    public List<PerformanceVerificationEventMessageStats> getReports(Duration duration, Instant stop);

    /**
     * @Returns statistics for messages sent from {@code stop.minus(duration)} to {@code stop}. Statistics are averaged
     * for each message accounted for and one statistic is returned.
     * 
     * Use {@link #getReports(Duration, Instant)} for per-message stats
     */
    public PerformanceVerificationEventStats getAverageReport(Duration duration, Instant stop);

    /**
     * @Returns a list of messages sent from {@code stop.minus(duration)} to {@code stop}.
     */
    public List<PerformanceVerificationEventMessage> getEventMessages(Duration duration, Instant stop);

}
