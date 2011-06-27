package com.cannontech.common.bulk.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import com.google.common.collect.Lists;

public class ArchiveDataAnalysisHelper {
    
    public static List<Instant> getListOfRelevantDateTimes(Interval dateTimeRange, Duration intervalLength) {
        List<Instant> endTimes = Lists.newArrayList();
        
        //first point of interest is one interval into the range
        Instant time = dateTimeRange.getStart().toInstant().plus(intervalLength);
        Instant endTime = dateTimeRange.getEnd().toInstant();
        
        while(!time.isAfter(endTime)) {
            endTimes.add(time);
            time = time.plus(intervalLength);
        }
        
        return endTimes;
    }
}
