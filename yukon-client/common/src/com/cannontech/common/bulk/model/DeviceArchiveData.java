package com.cannontech.common.bulk.model;

import java.util.List;

import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.google.common.collect.Lists;

public class DeviceArchiveData {
    private PaoIdentifier paoIdentifier;
    private Attribute attribute;
    private Interval archiveRange;
    private List<ArchiveData> archiveData = Lists.newArrayList();
    private List<ReadSequence> timeline;
    
    public DeviceArchiveData() {}
    
    public DeviceArchiveData(PaoIdentifier paoIdentifier, Attribute attribute, Interval archiveRange) {
        this.paoIdentifier = paoIdentifier;
        this.attribute = attribute;
        this.archiveRange = archiveRange;
    }
    
    public DeviceArchiveData(PaoIdentifier paoIdentifier, Attribute attribute, List<ArchiveData> archiveData, Interval archiveRange) {
        this.paoIdentifier = paoIdentifier;
        this.attribute = attribute;
        this.archiveData = archiveData;
        this.archiveRange = archiveRange;
    }
    
    public int getNumberOfIntervals() {
        return archiveData.size();
    }
    
    public Interval getArchiveRange() {
        return archiveRange;
    }

    public void setArchiveRange(Interval archiveRange) {
        this.archiveRange = archiveRange;
    }
    
    public List<ArchiveData> getArchiveData() {
        return archiveData;
    }
    
    public void setArchiveData(List<ArchiveData> archiveData) {
        this.archiveData = archiveData;
    }
    
    public void addArchiveData(ArchiveData data) {
        archiveData.add(data);
    }
    
    public void addAllArchiveData(List<ArchiveData> archiveDataList) {
        archiveData.addAll(archiveDataList);
    }
    
    public void setId(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public PaoIdentifier getId() {
        return paoIdentifier;
    }
    
    public ArchiveData getReadForDate(Instant date) {
        for(ArchiveData read : archiveData) {
            if(read.getIntervalStart().isEqual(date)) {
                return read;
            }
        }
        return null;
    }
    
    public int getHoleCount() {
        int count = 0;
        for (ArchiveData readStatus : archiveData) {
            if (readStatus.getReadType() == ReadType.DATA_MISSING) {
                count++;
            }
        }
        
        return count;
    }
    
    public void setTimeline(List<ReadSequence> timeline) {
        this.timeline = timeline;
    }

    public List<ReadSequence> getTimeline() {
        return timeline;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}