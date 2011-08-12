package com.cannontech.common.bulk.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;

import com.google.common.collect.Lists;

public class ArchiveDataAnalysisHelper {
    
    public static List<Instant> getListOfRelevantDateTimes(Interval dateTimeRange, Period intervalPeriod) {
        List<Instant> endTimes = Lists.newArrayList();
        
        Instant startTime = dateTimeRange.getStart().toInstant();
        Instant endTime = dateTimeRange.getEnd().toInstant();
        //first point of interest is one interval into the range
        Instant time = startTime.plus(intervalPeriod.toDurationFrom(startTime));
        
        while(!time.isAfter(endTime)) {
            endTimes.add(time);
            Duration intervalDuration = intervalPeriod.toDurationFrom(time);
            time = time.plus(intervalDuration);
        }
        
        return endTimes;
    }
}
