package com.cannontech.common.bulk.service;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.SystemDateFormattingService;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisHelper {
    private SystemDateFormattingService systemDateFormattingService;
    
    public List<Instant> getListOfRelevantDateTimes(Interval dateTimeRange, Period intervalPeriod) {
        DateTimeZone zone = DateTimeZone.forTimeZone(systemDateFormattingService.getSystemTimeZone());
        List<Instant> result = Lists.newArrayList();
        
        Instant startTime = dateTimeRange.getStart().toInstant();
        Instant endTime = dateTimeRange.getEnd().toInstant();
        //first point of interest is one interval into the range
        DateTime time = startTime.toDateTime(zone);
        time = time.plus(intervalPeriod);
        
        while(!time.isAfter(endTime)) {
            result.add(time.toInstant());
            time = time.plus(intervalPeriod);
        }
        
        return result;
    }
    
    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }
}
