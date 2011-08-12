package com.cannontech.common.bulk.model;

import java.util.Date;

import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class ArchiveDataAnalysisBackingBean {
    private DeviceCollection deviceCollection;
    private BuiltInAttribute selectedAttribute;
    private Period selectedInterval;
    private Date startDate;
    private Date stopDate;
    private Boolean excludeBadQualities;
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }
    
    public BuiltInAttribute getSelectedAttribute() {
        return selectedAttribute;
    }
    
    public void setSelectedAttribute(BuiltInAttribute selectedAttribute) {
        this.selectedAttribute = selectedAttribute;
    }
    
    public Period getSelectedInterval() {
        return selectedInterval;
    }
    
    public void setSelectedInterval(Period selectedInterval) {
        this.selectedInterval = selectedInterval;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public Instant getStartInstant() {
        return new Instant(startDate);
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getStopDate() {
        return stopDate;
    }
    
    public Instant getStopInstant() {
        return new Instant(stopDate);
    }
    
    public Interval getDateRange() {
        return new Interval(getStartInstant(), getStopInstant());
    }
    
    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }
    
    public Boolean getExcludeBadQualities() {
        return excludeBadQualities;
    }
    
    public void setExcludeBadQualities(Boolean excludeBadQualities) {
        this.excludeBadQualities = excludeBadQualities;
    }
}
