package com.cannontech.common.bulk.model;

import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class Analysis {
    private BuiltInAttribute attribute;
    private int analysisId;
    private Interval dateTimeRange;
    private Period intervalPeriod;
    private long lastChangeId;
    private Instant runDate;
    private boolean excludeBadPointQualities;
    private AdaStatus status;
    private String statusId;
    
    public Analysis() {}
    
    protected Analysis(Analysis analysis) {
        this.attribute = analysis.attribute;
        this.analysisId = analysis.analysisId;
        this.dateTimeRange = analysis.dateTimeRange;
        this.intervalPeriod = analysis.intervalPeriod;
        this.lastChangeId = analysis.lastChangeId;
        this.runDate = analysis.runDate;
        this.excludeBadPointQualities = analysis.excludeBadPointQualities;
        this.status = analysis.status;
        this.statusId = analysis.statusId;
    }
    
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
    
    public void setIntervalPeriod(Period intervalPeriod) {
        this.intervalPeriod = intervalPeriod;
    }
    
    public Period getIntervalPeriod() {
        return intervalPeriod;
    }
    
    public void setLastChangeId(long lastChangeId) {
        this.lastChangeId = lastChangeId;
    }
    
    public long getLastChangeId() {
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

    public void setStatus(AdaStatus status) {
        this.status = status;
    }

    public AdaStatus getStatus() {
        return status;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
    
    public String getStatusId() {
        return statusId;
    }
}