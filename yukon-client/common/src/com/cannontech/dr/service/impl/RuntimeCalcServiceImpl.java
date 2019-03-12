package com.cannontech.dr.service.impl;

import static com.cannontech.common.util.TimeUtil.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cannontech.dr.service.RuntimeCalcService;

public class RuntimeCalcServiceImpl implements RuntimeCalcService {
    
    @Override
    public Map<DateTime, Integer> getHourlyRuntimeSeconds(List<DatedRuntimeStatus> statuses) {
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        
        // At least two statuses are needed to calculate any runtime
        if (statuses.size() < 2) {
            return hourlyRuntimeSeconds;
        }
        
        for (int index = 1; index < statuses.size(); index++) {
            DatedRuntimeStatus previousStatus = statuses.get(index - 1);
            DatedRuntimeStatus currentStatus = statuses.get(index);
            
            // If we encounter two consecutive statuses that are out of date order, throw an exception
            if (previousStatus.getDate().isAfter(currentStatus.getDate())) {
                throw new IllegalArgumentException("Runtime statuses are not ordered by time.");
            }
            
            // Calculate runtimes between each consecutive pair of statuses and add to the totals
            Map<DateTime, Integer> partialRuntime = getHourlyRuntimeSeconds(previousStatus, currentStatus);
            sumRuntimes(hourlyRuntimeSeconds, partialRuntime);
        }
        
        return hourlyRuntimeSeconds;
    }
    
    /**
     * Calculates hourly runtimes for all hours at least partly between two dated states. If the previousState is 
     * STOPPED, all hours in the return object will have 0 runtime. Hours at the start and end of the measurement period
     * will reflect only the runtime that occurred within the measurement period, not the full runtime for that hour.
     * @param previousState The datedRuntimeStatus at the start of the measurement period.
     * @param currentState The datedRuntimeStatus at the end of the measurement period.
     * @return The map of hours and runtime, in seconds, that occurred between the start and end of the measurement 
     * period.
     */
    private Map<DateTime, Integer> getHourlyRuntimeSeconds(DatedRuntimeStatus previousStatus, 
                                                           DatedRuntimeStatus currentStatus) {
        
        // Only record runtime if the status was running during this period
        boolean isRuntime = previousStatus.isActive();
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        
        DateTime startOfCurrentHour = getStartOfHour(currentStatus.getDate());
        DateTime startOfRuntimePeriod = previousStatus.getDate();
        
        if (startOfRuntimePeriod.isBefore(startOfCurrentHour)) {
            // This period crosses hour boundaries and must be split
            hourlyRuntimeSeconds = calculateRuntimeAcrossHours(isRuntime, startOfRuntimePeriod, currentStatus.getDate());
        } else {
            // This period is contained within an hour
            int runtimeSeconds = getRuntimeSecondsWithinHour(isRuntime, previousStatus.getDate(), currentStatus.getDate());
            //add runtime as hour-ending
            hourlyRuntimeSeconds.put(roundUpToNextHour(currentStatus.getDate()), runtimeSeconds);
        }
        
        return hourlyRuntimeSeconds;
    }
    
    /**
     * Performs the calculation of runtime for a start and end dateTime that fall across more than one hour. Calculates
     * runtimes for all hours that are at least partially within the measurement period. In the return object, hours at 
     * the start and end of the period will only show the runtime that fell within the period, not the full runtime for 
     * that hour.
     * @param isRuntime True if the calculation is for runtime, false if it's for "off" time. When this parameter is
     * false, the method will return 0 for all hours in the period, since no runtime will occur when the device is off.
     * @param startOfRuntimePeriod The datetime of the start of the measurement period.
     * @param endOfRuntimePeriod The datetime of the end of the measurement period.
     * @return The map of hours and runtime, in seconds, that occurred between the start and end of the measurement 
     * period.
     */
    private Map<DateTime, Integer> calculateRuntimeAcrossHours(boolean isRuntime, DateTime startOfRuntimePeriod, DateTime endOfRuntimePeriod) {
        
        // For return values
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        
        // For calculation
        DateTime startOfInternalPeriod = startOfRuntimePeriod;
        DateTime endOfInternalPeriod = roundUpToNextHour(startOfRuntimePeriod);
        DateTime startOfFinalHour = getStartOfHour(endOfRuntimePeriod);
        
        // Calculate all hours prior to the last one
        while (endOfInternalPeriod.isBefore(startOfFinalHour) || endOfInternalPeriod.equals(startOfFinalHour)) {
            int runtimeSeconds = getRuntimeSecondsWithinHour(isRuntime, startOfInternalPeriod, endOfInternalPeriod);
            //add runtime as hour-ending
            hourlyRuntimeSeconds.put(roundUpToNextHour(startOfInternalPeriod), runtimeSeconds);
            startOfInternalPeriod = endOfInternalPeriod;
            endOfInternalPeriod = endOfInternalPeriod.plus(Duration.standardHours(1));
        }
        
        // Calculate the last hour (or skip if the end of the period is exactly at the top of the hour)
        if (!startOfFinalHour.equals(endOfRuntimePeriod)) {
            int runtimeSeconds = getRuntimeSecondsWithinHour(isRuntime, startOfFinalHour, endOfRuntimePeriod);
            //add runtime as hour-ending
            hourlyRuntimeSeconds.put(roundUpToNextHour(startOfFinalHour), runtimeSeconds);
        }
        
        return hourlyRuntimeSeconds;
    }
    
    /**
     * Performs the simple calculation of runtime for a start and end dateTime that fall within an hour. This is a
     * straight subtraction of "end time seconds" minus "start time seconds".
     * @param isRuntime True if the calculation is for runtime, false if it's for "off" time. When this parameter is
     * false, the method will return 0, since no runtime will occur when the device is off.
     * @param start The datetime of the start of the measurement period.
     * @param end The datetime of the end of the measurement period.
     * @return The runtime, in seconds, between the start and stop; or if isRuntime == false, 0.
     */
    private int getRuntimeSecondsWithinHour(boolean isRuntime, DateTime start, DateTime end) {
        if (isRuntime) {
            long periodMillis = end.getMillis() - start.getMillis();
            int periodSeconds = (int) periodMillis / 1000;
            return periodSeconds;
        } else {
            return 0;
        }
    }
    
    /**
     * Adds entries from one map of dated runtimes to another. (If an entry already exists for a date in totalRuntimes,
     * and there is runtime for that date in partialRuntimes, the value from partialRuntimes will be added to the value
     * in totalRuntimes. 
     * @param totalRuntimes The map of all dated runtimes.
     * @param partialRuntimes An incomplete map of dated runtimes to be added to totalRuntimes.
     */
    private void sumRuntimes(Map<DateTime, Integer> totalRuntimes, Map<DateTime, Integer> partialRuntimes) {
        for (DateTime date : partialRuntimes.keySet()) {
            if (totalRuntimes.containsKey(date)) {
                // Some runtime has been recorded for this hour. Add the partial value to it.
                Integer totalRuntimeSeconds = totalRuntimes.get(date);
                Integer partialRuntimeSeconds = partialRuntimes.get(date);
                totalRuntimes.put(date, totalRuntimeSeconds + partialRuntimeSeconds);
            } else {
                // No runtime has been recorded yet for this hour. Put the new value.
                totalRuntimes.put(date, partialRuntimes.get(date));
            }
        }
    }
}
