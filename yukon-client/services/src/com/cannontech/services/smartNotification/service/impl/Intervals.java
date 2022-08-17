package com.cannontech.services.smartNotification.service.impl;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.Iterables;

/**
 * This class is responsible for loading and managing smart notification intervals.
 */
public final class Intervals {
    private static final Logger log = YukonLogManager.getLogger(Intervals.class);
    private final Set<Integer> intervals = new TreeSet<>();

    public Intervals(String intervalStr, String defaultIntervalStr) {
        // Try to parse the specified intervals. If that fails, parse the defaults
        if (!parseIntervals(intervalStr)) {
            parseIntervals(defaultIntervalStr);
        }
    }
    
    /**
     * Attempt to parse the specified String into intervals.
     * @return True if the parse succeeded, otherwise false.
     */
    private boolean parseIntervals(String intervalStr) {
        try {
            // Load intervals from string representation
            intervals.clear();
            log.debug("Parsing intervals string: \"" + intervalStr + "\"");
            intervals.addAll(Stream.of(intervalStr.split(","))
                                   .map(String::trim)
                                   .map(Integer::parseInt)
                                   .collect(Collectors.toSet()));
            
            // If there is an invalid interval, return false
            if (intervals.iterator().next() < 0) {
                return false;
            }
            
            // Otherwise return true
            log.info("Smart Notification Intervals:" + intervals);
            return true;
        } catch (Exception e) {
            // If there's any exception, log and return false
            if (log.isTraceEnabled()) {
                log.trace("Error loading intervals \"" + intervals, e);
            } else {
                log.warn("Error loading intervals \"" + intervals + ": " + e);
            }
            return false;
        }
    }
    
    /**
     * Given an interval value, find the next interval in the series.
     * @return The next interval value after the specified current value, if the current value is less than the last
     * value in the series. Otherwise, the last value is returned.
     */
    public Integer getNextInterval(int currentInterval) {
        Set<Integer> allIntervals = new TreeSet<>(intervals);
        allIntervals.removeIf(interval -> interval <= currentInterval);
        if (allIntervals.isEmpty()) {
            return Iterables.getLast(intervals);
        } else {
            return allIntervals.iterator().next();
        }
    }
    
    /**
     * Get the first interval value in the series.
     */
    public Integer getFirstInterval() {
        return intervals.iterator().next();
    }
}