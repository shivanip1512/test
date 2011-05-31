package com.cannontech.web.bulk.model;

import java.util.List;

import org.joda.time.Interval;

import com.cannontech.common.pao.PaoIdentifier;

public class DeviceArchiveData {
    private PaoIdentifier id;
    private Interval archiveRange;
    private List<ArchiveData> archiveData;
    private List<PixelData> timeline;
    
    public DeviceArchiveData() {}
    
    public DeviceArchiveData(PaoIdentifier id, List<ArchiveData> archiveData, Interval archiveRange) {
        this.setId(id);
        this.archiveData = archiveData;
        this.archiveRange = archiveRange;
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

    public void setId(PaoIdentifier id) {
        this.id = id;
    }

    public PaoIdentifier getId() {
        return id;
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
    
    public void setTimeline(List<PixelData> timeline) {
        this.timeline = timeline;
    }

    public List<PixelData> getTimeline() {
        return timeline;
    }

}