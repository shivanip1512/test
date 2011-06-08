package com.cannontech.common.bulk.model;

import org.joda.time.Instant;
import org.joda.time.Interval;


public class ArchiveData {
    private Interval archiveRange;
    private ReadType readType;
    private Integer changeId;
    private int paoId;
    
    public ArchiveData(Interval archiveRange, ReadType readType, Integer changeId){
        this.archiveRange = archiveRange;
        this.readType = readType;
        this.changeId = changeId;
    }
    
    public boolean isDataPresent() {
        return readType == ReadType.DATA_PRESENT;
    }
    
    public Interval getArchiveRange() {
        return archiveRange;
    }
    
    public ReadType getReadType() {
        return readType;
    }

    public void setArchiveRange(Interval archiveRange) {
        this.archiveRange = archiveRange;
    }

    public void setReadType(ReadType readType) {
        this.readType = readType;
    }
    
    public Integer getChangeId() {
        return changeId;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
    public int getPaoId() {
        return paoId;
    }
    
    public Instant getIntervalStart() {
        return archiveRange.getStart().toInstant();
    }
}