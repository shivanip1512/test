package com.cannontech.web.amr.waterLeakReport.model;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.Range;

public class WaterLeakReportFilter {

    public final static int DEFAULT_FROM_HOURS = 49;
    public final static int DEFAULT_TO_HOURS = 25;

    private DeviceCollection deviceCollection;
    private Instant fromInstant;
    private Instant toInstant;
    private Double threshold = 0.0;
    private boolean includeDisabledPaos;
    
    {
        fromInstant = new Instant().minus(Duration.standardHours(DEFAULT_FROM_HOURS));
        toInstant = new Instant().minus(Duration.standardHours(DEFAULT_TO_HOURS));

        // Normalize to zero-out the minutes and seconds (since water nodes report on the exact hour)
        fromInstant = fromInstant.minus(Duration.standardMinutes(fromInstant.get(DateTimeFieldType.minuteOfHour())));
        fromInstant = fromInstant.minus(Duration.standardSeconds(fromInstant.get(DateTimeFieldType.secondOfMinute())));
        fromInstant = fromInstant.minus(fromInstant.get(DateTimeFieldType.millisOfSecond()));
        toInstant = toInstant.minus(Duration.standardMinutes(toInstant.get(DateTimeFieldType.minuteOfHour())));
        toInstant = toInstant.minus(Duration.standardSeconds(toInstant.get(DateTimeFieldType.secondOfMinute())));
        toInstant = toInstant.minus(toInstant.get(DateTimeFieldType.millisOfSecond()));
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public Instant getFromInstant() {
        return fromInstant;
    }

    public void setFromInstant(Instant fromInstant) {
        this.fromInstant = fromInstant;
    }

    public Instant getToInstant() {
        return toInstant;
    }

    public void setToInstant(Instant toInstant) {
        this.toInstant = toInstant;
    }

    public Range<Instant> getRange() {
        return Range.inclusive(fromInstant, toInstant);
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