package com.cannontech.amr.rfn.dataStreaming.model;

import java.util.Date;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Instant;

import com.cannontech.common.device.streaming.model.BehaviorReportStatus;

public class DiscrepancyResult {
    private String paoName;
    private int deviceId;
    private DataStreamingConfig expected;
    private DataStreamingConfig actual;
    private BehaviorReportStatus status;
    private Instant lastCommunicated;
    private boolean displayRemove;
    private boolean displayRead;

    public DataStreamingConfig getExpected() {
        return expected;
    }

    public void setExpected(DataStreamingConfig expected) {
        this.expected = expected;
    }

    public BehaviorReportStatus getStatus() {
        return status;
    }

    public void setStatus(BehaviorReportStatus status) {
        this.status = status;
    }

    public Instant getLastCommunicated() {
        return lastCommunicated;
    }

    public void setLastCommunicated(Instant lastCommunicated) {
        this.lastCommunicated = lastCommunicated;
    }

    public DataStreamingConfig getActual() {
        return actual;
    }

    public void setActual(DataStreamingConfig actual) {
        this.actual = actual;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    
    public boolean displayRemove() {
        return displayRemove;
    }

    public void setDisplayRemove(boolean displayRemove) {
        this.displayRemove = displayRemove;
    }
    
    public void setDisplayRead(boolean displayRead) {
        this.displayRead = displayRead;
    }
    
    public boolean displayRead() {
        return displayRead;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("paoName", paoName);
        builder.append("deviceId", deviceId);
        builder.append("status", status);
        builder.append("lastCommunicated", new Date(lastCommunicated.getMillis()));
        return builder.toString();
    }
}
