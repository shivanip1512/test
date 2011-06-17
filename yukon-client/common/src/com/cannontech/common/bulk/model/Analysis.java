package com.cannontech.common.bulk.model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.Lists;

public class Analysis {
    private BuiltInAttribute attribute;
    private int analysisId;
    private Interval dateTimeRange;
    private Duration intervalLength;
    
    private int lastChangeId;
    private Instant runDate;
    private boolean excludeBadPointQualities;
    
    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }
    
    public int getAnalysisId() {
        return analysisId;
    }
    
    public void setAnalysisId(int analysisId) {
        this.analysisId = analysisId;
    }
    
    public Interval getDateTimeRange() {
        return dateTimeRange;
    }
    
    public void setDateTimeRange(Interval dateTimeRange) {
        this.dateTimeRange = dateTimeRange;
    }
    
    public void setIntervalLength(Duration intervalLength) {
        this.intervalLength = intervalLength;
    }
    
    public Duration getIntervalLength() {
        return intervalLength;
    }
    
    public void setLastChangeId(int lastChangeId) {
        this.lastChangeId = lastChangeId;
    }
    
    public int getLastChangeId() {
        return lastChangeId;
    }
    
    public void setExcludeBadPointQualities(boolean excludeBadPointQualities) {
        this.excludeBadPointQualities = excludeBadPointQualities;
    }
    
    public boolean isExcludeBadPointQualities() {
        return excludeBadPointQualities;
    }
    
    public void setRunDate(Instant runDate) {
        this.runDate = runDate;
    }
    
    public Instant getRunDate() {
        return runDate;
    }
    
    public List<Instant> getIntervalStartTimes() {
        List<Instant> dateTimeList = Lists.newArrayList();
        
        Instant nextIntervalStartTime = dateTimeRange.getStart().toInstant();
        Instant endTime = dateTimeRange.getEnd().toInstant();
        dateTimeList.add(nextIntervalStartTime);
        boolean loop = true;
        
        while(loop) {
            nextIntervalStartTime = nextIntervalStartTime.plus(intervalLength);
            if(nextIntervalStartTime.isBefore(endTime)) {
                dateTimeList.add(nextIntervalStartTime);
            } else {
                loop = false;
            }
        }
        
        return dateTimeList;
    }
    
    public Interval getDateTimeRangeForDisplay() {
        DateTime newStart = dateTimeRange.getStart().minus(intervalLength);
        DateTime newEnd = dateTimeRange.getEnd().minus(intervalLength);

        return new Interval(newStart, newEnd);
    }
}