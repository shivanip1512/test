package com.cannontech.common.bulk.model;

import org.joda.time.Interval;

public class ArchiveData {
    private Interval archiveRange;
    private ReadType readType;
    private Long changeId;
    
    public ArchiveData(Interval archiveRange, ReadType readType, Long changeId){
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
    
    public Long getChangeId() {
        return changeId;
    }
}