package com.cannontech.services.smartNotification.service.impl;

import org.joda.time.DateTime;
import org.joda.time.Instant;

public final class WaitTime {
    private static Intervals intervals;
    
    /**
     * This should be set before any instances of WaitTime are used.
     */
    public static void setIntervals(Intervals intervals) {
        WaitTime.intervals = intervals;
    }
    
    /**
     * Returns first run time info.
     */
    public static WaitTime getFirst(Instant now) {
        int newInterval = intervals.getFirstInterval();
        DateTime newRunTime = now.toDateTime().plusMinutes(newInterval);
        return new WaitTime(newInterval, 0, newRunTime);
    }
    
    private int interval;
    private int previousInterval;
    private DateTime runTime;

    public WaitTime(int interval, int previousInterval, DateTime runTime) {
        this.interval = interval;
        this.previousInterval = previousInterval;
        this.runTime = runTime;
    }

    public int getInterval() {
        return interval;
    }

    public int getPreviousInterval() {
        return previousInterval;
    }
    
    public DateTime getRunTime() {
        return runTime;
    }

    /**
     * Returns next run time info.
     */
    public WaitTime getNext(Instant now) {
        int newInterval = intervals.getNextInterval(interval);
        DateTime newRunTime = now.toDateTime().plusMinutes(newInterval);
        return new WaitTime(newInterval, interval, newRunTime);
    }
    
    @Override
    public String toString(){
        return "[Run Time="+runTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss")+" - Interval="+interval+" minutes]";
    }
}
