package com.cannontech.web.bulk.model;

import org.joda.time.Interval;

public class ArchiveData {
    private Interval archiveRange;
    private ReadType readType;
    
    public ArchiveData(Interval archiveRange, ReadType readType){
        this.archiveRange = archiveRange;
        this.readType = readType;
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

}