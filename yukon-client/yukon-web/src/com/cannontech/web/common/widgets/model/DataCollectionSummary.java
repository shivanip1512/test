package com.cannontech.web.common.widgets.model;

import org.joda.time.Instant;

public class DataCollectionSummary {

    private DataCollectionDetail available;
    private DataCollectionDetail expected;
    private DataCollectionDetail outdated;
    private DataCollectionDetail unavailable;
    private Instant collectionTime;

    public DataCollectionDetail getAvailable() {
        return available;
    }

    public void setAvailable(DataCollectionDetail available) {
        this.available = available;
    }

    public DataCollectionDetail getExpected() {
        return expected;
    }

    public void setExpected(DataCollectionDetail expected) {
        this.expected = expected;
    }

    public DataCollectionDetail getOutdated() {
        return outdated;
    }

    public void setOutdated(DataCollectionDetail outdated) {
        this.outdated = outdated;
    }

    public DataCollectionDetail getUnavailable() {
        return unavailable;
    }

    public void setUnavailable(DataCollectionDetail unavailable) {
        this.unavailable = unavailable;
    }

    public Instant getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Instant collectionTime) {
        this.collectionTime = collectionTime;
    }
}
