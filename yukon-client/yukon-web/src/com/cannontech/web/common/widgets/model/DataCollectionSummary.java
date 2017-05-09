package com.cannontech.web.common.widgets.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

public class DataCollectionSummary {

    private DataCollectionDetail available;
    private DataCollectionDetail expected;
    private DataCollectionDetail outdated;
    private DataCollectionDetail unavailable;
    private Instant collectionTime;
    
    public DataCollectionSummary(Instant collectionTime){
        this.collectionTime = collectionTime;
    }

    public DataCollectionDetail getAvailable() {
        return available;
    }

    public void setAvailable(int deviceCount) {
        this.available =  new DataCollectionDetail(deviceCount);
    }

    public DataCollectionDetail getExpected() {
        return expected;
    }

    public void setExpected(int deviceCount) {
        this.expected = new DataCollectionDetail(deviceCount);
    }

    public DataCollectionDetail getOutdated() {
        return outdated;
    }

    public void setOutdated(int deviceCount) {
        this.outdated = new DataCollectionDetail(deviceCount);
    }

    public DataCollectionDetail getUnavailable() {
        return unavailable;
    }

    public void setUnavailable(int deviceCount) {
        this.unavailable = new DataCollectionDetail(deviceCount);
    }

    public Instant getCollectionTime() {
        return collectionTime;
    }
    
    public void calculatePrecentages() {
        Integer total = available.getDeviceCount() + expected.getDeviceCount() + outdated.getDeviceCount()
            + unavailable.getDeviceCount();
        available.calculatePrecentage(total);
        expected.calculatePrecentage(total);
        outdated.calculatePrecentage(total);
        unavailable.calculatePrecentage(total);
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("available="+ available);
        tsb.append("expected="+ expected);
        tsb.append("outdated="+ outdated);
        tsb.append("unavailable="+ unavailable);
        return tsb.toString();
    }
}
