package com.cannontech.dr.dao;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;

public interface PerformanceVerificationDao {

    /**
     * Returns statistics for messages sent from stop-duration to stop. Statistics returned are per-message
     */
    public List<PerformanceVerificationEventMessageStats> getReports(Duration duration, Instant stop);

    /**
     * Returns statistics for messages sent from stop-duration to stop. Statistics are averaged for each message
     * accounted for and one statistic is returned. Use {@link #getReports(Duration, Instant)} for per-message stats
     */
    public PerformanceVerificationEventStats getAverageReport(Duration duration, Instant stop);

}
