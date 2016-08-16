package com.cannontech.web.rfn.dataStreaming.model;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Instant;

import com.cannontech.common.device.streaming.model.BehaviorReportStatus;

public class DiscrepancyResult {
    private String paoName;
    private int deviceId;
    private int behaviorReportId;
    private DataStreamingConfig expected;
    private DataStreamingConfig actual;
    private BehaviorReportStatus status;
    private Instant lastCommunicated;

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

    public int getBehaviorReportId() {
        return behaviorReportId;
    }

    public void setBehaviorReportId(int behaviorReportId) {
        this.behaviorReportId = behaviorReportId;
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
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("paoName", paoName);
        builder.append("deviceId", deviceId);
        builder.append("behaviorReportId", behaviorReportId);
        builder.append("status", status);
        builder.append("lastCommunicated", lastCommunicated);
        return builder.toString();
    }
}
