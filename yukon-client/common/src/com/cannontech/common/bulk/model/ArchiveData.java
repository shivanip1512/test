package com.cannontech.common.bulk.model;

import org.joda.time.Interval;

import com.cannontech.common.pao.PaoIdentifier;


public class ArchiveData {
    private Interval archiveRange;
    private ReadType readType;
    private Integer changeId;
    private PaoIdentifier paoIdentifier;
    
    public ArchiveData(Interval archiveRange, ReadType readType, Integer changeId, PaoIdentifier paoIdentifier){
        this.archiveRange = archiveRange;
        this.readType = readType;
        this.changeId = changeId;
        this.paoIdentifier = paoIdentifier;
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
    
    public PaoIdentifier getPaoId() {
        return paoIdentifier;
    }
}