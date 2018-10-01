package com.cannontech.dr.nest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

public class NestControlHistory {
    private int id;
    private String key;
    private Instant startTime;
    private Instant stopTime;
    private Instant cancelRequestTime;
    private String cancelResponse;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    public Instant getStopTime() {
        return stopTime;
    }
    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }
    public Instant getCancelRequestTime() {
        return cancelRequestTime;
    }
    public void setCancelRequestTime(Instant cancelRequestTime) {
        this.cancelRequestTime = cancelRequestTime;
    }
    public String getCancelResponse() {
        return cancelResponse;
    }
    public void setCancelResponse(String cancelResponse) {
        this.cancelResponse = cancelResponse;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
