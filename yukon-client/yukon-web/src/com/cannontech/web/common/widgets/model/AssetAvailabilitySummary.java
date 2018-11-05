package com.cannontech.web.common.widgets.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AssetAvailabilitySummary {
    
    private AssetAvailabilityDetail active;
    private AssetAvailabilityDetail unavailabile;
    private AssetAvailabilityDetail inactive;
    private AssetAvailabilityDetail optedOut;
    private Instant collectionTime;
    
    public AssetAvailabilitySummary (Instant collectionTime) {
        this.collectionTime = collectionTime;
    }

    public AssetAvailabilityDetail getActive() {
        return active;
    }

    public void setActive(int deviceCount) {
        this.active = new AssetAvailabilityDetail(deviceCount);
    }

    public AssetAvailabilityDetail getUnavailabile() {
        return unavailabile;
    }

    public void setUnavailabile(int deviceCount) {
        this.unavailabile = new AssetAvailabilityDetail(deviceCount);
    }

    public AssetAvailabilityDetail getInactive() {
        return inactive;
    }

    public void setInactive(int deviceCount) {
        this.inactive = new AssetAvailabilityDetail(deviceCount);
    }

    public AssetAvailabilityDetail getOptedOut() {
        return optedOut;
    }

    public void setOptedOut(int deviceCount) {
        this.optedOut = new AssetAvailabilityDetail(deviceCount);
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
        unavailabile.calculatePrecentage(total);
        inactive.calculatePrecentage(total);
        optedOut.calculatePrecentage(total);
    }
    
    public int getTotalDeviceCount(){
        return active.getDeviceCount() + unavailabile.getDeviceCount() + inactive.getDeviceCount()
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
        tsb.append("available="+ active);
        tsb.append("expected="+ unavailabile);
        tsb.append("outdated="+ inactive);
        tsb.append("unavailable="+ optedOut);

        return tsb.toString();
    }

}
