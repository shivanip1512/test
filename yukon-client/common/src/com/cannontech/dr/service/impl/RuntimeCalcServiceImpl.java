package com.cannontech.dr.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.cannontech.dr.service.RuntimeCalcService;
import com.cannontech.dr.service.RelayLogInterval;

public class RuntimeCalcServiceImpl implements RuntimeCalcService {
    
    @Override
    public Map<DateTime, Integer> getHourlyRuntimeSeconds(Iterable<DatedRuntimeStatus> statuses) {
        return getIntervalRelayLogs(statuses, RelayLogInterval.LOG_60_MINUTE);
    }
    
    @Override
    public Map<DateTime, Integer> getIntervalRelayLogs(Iterable<? extends DatedStatus> statuses, RelayLogInterval interval) {
        Map<DateTime, Integer> intervalRelaySeconds = new HashMap<>();
        
        Iterator<? extends DatedStatus> iterator = statuses.iterator();
        
        if (iterator.hasNext()) {
            DatedStatus previousStatus = iterator.next();
            
            while(iterator.hasNext()) {
                DatedStatus currentStatus = iterator.next();
                
                // If we encounter two consecutive statuses that are out of date order, throw an exception
                if (previousStatus.getDate().isAfter(currentStatus.getDate())) {
                    throw new IllegalArgumentException("Runtime statuses are not ordered by time.");
                }
                
                // Calculate runtimes between each consecutive pair of statuses and add to the totals
                Map<DateTime, Integer> partialRuntime = getIntervalActiveSeconds(previousStatus, currentStatus, interval);
                sumRuntimes(intervalRelaySeconds, partialRuntime);
                
                previousStatus = currentStatus;
            }
        }
        
        return intervalRelaySeconds;
    }

    /**
     * Calculates interval runtimes for all intervals at least partly between two dated states. If the previousState is 
     * STOPPED, all intervals in the return object will have 0 runtime. Intervals at the start and end of the measurement period
     * will reflect only the runtime that occurred within the measurement period, not the full runtime for that intervals.
     * @param interval The interval at which to generate activity logs.  
     * @param previousState The datedRuntimeStatus at the start of the measurement period.
     * @param currentState The datedRuntimeStatus at the end of the measurement period.
     * @return The map of intervals and runtime, in seconds, that occurred between the start and end of the measurement 
     * period.
     */
    private static Map<DateTime, Integer> getIntervalActiveSeconds(DatedStatus previousStatus, 
                                                                   DatedStatus currentStatus, RelayLogInterval interval) {
        
        // Only record runtime if the status was running during this period
        boolean isActive = previousStatus.isActive();
        
        Map<DateTime, Integer> intervalRelaySeconds = new HashMap<>();
        
        DateTime startOfCurrentInterval = interval.start(currentStatus.getDate());
        DateTime startOfRuntimePeriod = previousStatus.getDate();
        
        if (startOfRuntimePeriod.isBefore(startOfCurrentInterval)) {
            // This period crosses interval boundaries and must be split
            intervalRelaySeconds = calculateActiveTimeAcrossIntervals(isActive, startOfRuntimePeriod, currentStatus.getDate(), interval);
        } else {
            // This period is contained within an interval
            int runtimeSeconds = getActiveSecondsWithinInterval(isActive, previousStatus.getDate(), currentStatus.getDate());
            //add runtime as interval-ending
            intervalRelaySeconds.put(interval.end(currentStatus.getDate()), runtimeSeconds);
        }
        
        return intervalRelaySeconds;
    }
    
    /**
     * Performs the calculation of runtime for a start and end dateTime that fall across more than one interval. Calculates
     * runtimes for all intervals that are at least partially within the measurement period. In the return object, intervals at 
     * the start and end of the period will only show the runtime that fell within the period, not the full runtime for 
     * that interval.
     * @param isActive True if the calculation is for active time, false if it's for "off" time. When this parameter is
     * false, the method will return 0 for all intervals in the period, since no activity will occur when the device is off.
     * @param startOfRuntimePeriod The datetime of the start of the measurement period.
     * @param endOfRuntimePeriod The datetime of the end of the measurement period.
     * @param interval The interval at which to generate activity logs.  
     * @return The map of intervals and runtime, in seconds, that occurred between the start and end of the measurement 
     * period.
     */
    private static Map<DateTime, Integer> calculateActiveTimeAcrossIntervals(boolean isActive, DateTime startOfRuntimePeriod, DateTime endOfRuntimePeriod, RelayLogInterval interval) {
        
        // For return values
        Map<DateTime, Integer> intervalRuntimeSeconds = new HashMap<>();
        
        // For calculation
        DateTime startOfInternalPeriod = startOfRuntimePeriod;
        DateTime endOfInternalPeriod = interval.end(startOfRuntimePeriod);
        DateTime startOfFinalInterval = interval.start(endOfRuntimePeriod);
        
        // Calculate all intervals prior to the last one
        while (endOfInternalPeriod.isBefore(startOfFinalInterval) || endOfInternalPeriod.equals(startOfFinalInterval)) {
            int runtimeSeconds = getActiveSecondsWithinInterval(isActive, startOfInternalPeriod, endOfInternalPeriod);
            //add runtime as interval-ending
            intervalRuntimeSeconds.put(interval.end(startOfInternalPeriod), runtimeSeconds);
            startOfInternalPeriod = endOfInternalPeriod;
            endOfInternalPeriod = endOfInternalPeriod.plus(interval.getDuration());
        }
        
        // Calculate the last interval (or skip if the end of the period is exactly at the start of the interval)
        if (!startOfFinalInterval.equals(endOfRuntimePeriod)) {
            int runtimeSeconds = getActiveSecondsWithinInterval(isActive, startOfFinalInterval, endOfRuntimePeriod);
            //add runtime as interval-ending
            intervalRuntimeSeconds.put(interval.end(startOfFinalInterval), runtimeSeconds);
        }
        
        return intervalRuntimeSeconds;
    }
    
    /**
     * Performs the simple calculation of runtime for a start and end dateTime that fall within an interval. This is a
     * straight subtraction of "end time seconds" minus "start time seconds".
     * @param isActive True if the calculation is for runtime, false if it's for "off" time. When this parameter is
     * false, the method will return 0, since no runtime will occur when the device is off.
     * @param start The datetime of the start of the measurement period.
     * @param end The datetime of the end of the measurement period.
     * @return The runtime, in seconds, between the start and stop; or if isRuntime == false, 0.
     */
    private static int getActiveSecondsWithinInterval(boolean isActive, DateTime start, DateTime end) {
        if (!isActive) {
            return 0;
        }
        return Seconds.secondsBetween(start, end).getSeconds();
    }
    
    /**
     * Adds entries from one map of dated runtimes to another. (If an entry already exists for a date in totalRuntimes,
     * and there is runtime for that date in partialRuntimes, the value from partialRuntimes will be added to the value
     * in totalRuntimes. 
     * @param totalRuntimes The map of all dated runtimes.
     * @param partialRuntimes An incomplete map of dated runtimes to be added to totalRuntimes.
     */
    private static void sumRuntimes(Map<DateTime, Integer> totalRuntimes, Map<DateTime, Integer> partialRuntimes) {
        partialRuntimes.forEach((key, value) -> 
            totalRuntimes.merge(key, value, Integer::sum));
    }
}
