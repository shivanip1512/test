package com.cannontech.dr.service;

import java.util.Map;

import org.joda.time.DateTime;

import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.DatedStatus;

/**
 * A service for calculating runtimes for DR devices, based upon sequences of DatedRuntimeStatuses.
 */
public interface RuntimeCalcService {
    
    /**
     * Calculates hourly runtimes from a chronologically-ordered list of DatedRuntimeStatuses. Hours at the start and 
     * end of the measurement period will reflect only the runtime that occurred within the measurement period, not the 
     * full runtime for that hour.
     * @param statuses A datetime-ordered list of DatedRuntimeStatuses representing the relay state of a DR device over
     * a period of time.
     * @return The map of hours and runtime, in seconds, that occurred between the first and last status in the list.
     * @throws IllegalArgumentException If the list of statuses is not ordered chronologically.
     */
    public Map<DateTime, Integer> getHourlyRuntimeSeconds(Iterable<DatedRuntimeStatus> statuses);
    
    /**
     * Similar to getHourlyRuntimeSeconds, but for either shedtime or runtime, and using a caller-specified interval.
     * @param statuses A datetime-ordered list of DatedStatuses representing the activity state of a DR device over
     * a period of time.
     * @param interval The interval to use when constructing the data logs.
     * @return The map of intervals and active time, in seconds, that occurred between the first and last status in the list.
     * @throws IllegalArgumentException If the list of statuses is not ordered chronologically.
     */
    public Map<DateTime, Integer> getIntervalRelayLogs(Iterable<? extends DatedStatus> statuses, RelayLogInterval interval);
}
