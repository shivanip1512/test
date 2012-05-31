package com.cannontech.web.amr.waterLeakReport.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Hours;
import org.joda.time.LocalDate;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.ListBackingBean;

public class WaterLeakReportFilterBackingBean extends ListBackingBean {

    public final static int DEFAULT_FROM_HOURS = 49;
    public final static int DEFAULT_TO_HOURS = 25;

    private DeviceCollection deviceCollection;
    private DateTime fromDateTime;
    private DateTime toDateTime;
    private LocalDate fromLocalDate;
    private LocalDate toLocalDate;
    private int fromHour;
    private int toHour;
    private double threshold = 0.0;
    private boolean includeDisabledPaos;
    
    public WaterLeakReportFilterBackingBean() {/* Needed by Spring */}

    public WaterLeakReportFilterBackingBean(WaterLeakReportFilterBackingBean toCopy) {
        this.deviceCollection = toCopy.deviceCollection;
        this.fromDateTime = new DateTime(toCopy.fromDateTime);
        this.toDateTime = new DateTime(toCopy.toDateTime);
        this.fromLocalDate = new LocalDate(toCopy.fromLocalDate);
        this.toLocalDate = new LocalDate(toCopy.toLocalDate);
        this.fromHour = toCopy.fromHour;
        this.toHour = toCopy.toHour;
        this.threshold = toCopy.threshold;
        this.includeDisabledPaos = toCopy.includeDisabledPaos;
    }

    public WaterLeakReportFilterBackingBean(YukonUserContext userContext) {
        fromDateTime = new DateTime(userContext.getJodaTimeZone()).minus(Hours.hours(DEFAULT_FROM_HOURS));
        toDateTime = new DateTime(userContext.getJodaTimeZone()).minus(Hours.hours(DEFAULT_TO_HOURS));

        fromLocalDate = new LocalDate(fromDateTime);
        toLocalDate = new LocalDate(toDateTime);

        fromHour = fromDateTime.get(DateTimeFieldType.hourOfDay());
        toHour = toDateTime.get(DateTimeFieldType.hourOfDay());
        
        setItemsPerPage(10);
    }

    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public DateTime getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(DateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public DateTime getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(DateTime toDateTime) {
        this.toDateTime = toDateTime;
    }

    public LocalDate getFromLocalDate() {
        return fromLocalDate;
    }

    public void setFromLocalDate(LocalDate fromLocalDate) {
        this.fromLocalDate = fromLocalDate;
    }

    public LocalDate getToLocalDate() {
        return toLocalDate;
    }

    public void setToLocalDate(LocalDate toLocalDate) {
        this.toLocalDate = toLocalDate;
    }

    public int getFromHour() {
        return fromHour;
    }

    public void setFromHour(int fromHour) {
        this.fromHour = fromHour;
    }
    
    public int getToHour() {
        return toHour;
    }

    public void setToHour(int toHour) {
        this.toHour = toHour;
    }
    
    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isIncludeDisabledPaos() {
        return includeDisabledPaos;
    }

    public void setIncludeDisabledPaos(boolean includeDisabledPaos) {
        this.includeDisabledPaos = includeDisabledPaos;
    }

}