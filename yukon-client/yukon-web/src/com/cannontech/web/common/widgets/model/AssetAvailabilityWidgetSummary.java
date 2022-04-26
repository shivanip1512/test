package com.cannontech.web.common.widgets.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AssetAvailabilityWidgetSummary {
    
    private AssetAvailabilityDetail active;
    private AssetAvailabilityDetail unavailable;
    private AssetAvailabilityDetail inactive;
    private AssetAvailabilityDetail optedOut;
    private Instant collectionTime;
    
    public AssetAvailabilityWidgetSummary (Instant collectionTime) {
        this.collectionTime = collectionTime;
    }

    public AssetAvailabilityDetail getActive() {
        return active;
    }

    public void setActive(int deviceCount, Integer pointId) {
        this.active = new AssetAvailabilityDetail(deviceCount, pointId);
    }

    public AssetAvailabilityDetail getUnavailable() {
        return unavailable;
    }

    public void setUnavailable(int deviceCount, Integer pointId) {
        this.unavailable = new AssetAvailabilityDetail(deviceCount, pointId);
    }

    public AssetAvailabilityDetail getInactive() {
        return inactive;
    }

    public void setInactive(int deviceCount, Integer pointId) {
        this.inactive = new AssetAvailabilityDetail(deviceCount, pointId);
    }

    public AssetAvailabilityDetail getOptedOut() {
        return optedOut;
    }

    public void setOptedOut(int deviceCount, Integer pointId) {
        this.optedOut = new AssetAvailabilityDetail(deviceCount, pointId);
    }

    public Instant getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Instant collectionTime) {
        this.collectionTime = collectionTime;
    }
    
    public void calculatePrecentages() {
        int total = getTotalDeviceCount();
        active.calculatePrecentage(total);
        unavailable.calculatePrecentage(total);
        inactive.calculatePrecentage(total);
        optedOut.calculatePrecentage(total);
    }
    
    public int getTotalDeviceCount(){
        return active.getDeviceCount() + unavailable.getDeviceCount() + inactive.getDeviceCount()
        + optedOut.getDeviceCount();
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        if (collectionTime != null) {
            final DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
            String time = collectionTime.toString(df.withZone(DateTimeZone.getDefault()));
            tsb.append("collection time=" + time);
        }
        tsb.append("active="+ active);
        tsb.append("unavailabile="+ unavailable);
        tsb.append("inactive="+ inactive);
        tsb.append("optedOut="+ optedOut);

        return tsb.toString();
    }

}
