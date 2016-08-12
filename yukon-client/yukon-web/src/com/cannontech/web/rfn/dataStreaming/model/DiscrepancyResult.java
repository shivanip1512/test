package com.cannontech.web.rfn.dataStreaming.model;

import org.joda.time.Instant;

import com.cannontech.common.device.streaming.model.BehaviorReportStatus;
import com.cannontech.common.rfn.model.RfnDevice;

public class DiscrepancyResult {
    private RfnDevice meter;
    private DataStreamingConfig expected;
    private DataStreamingConfig actual;
    private BehaviorReportStatus status;
    private Instant lastCommunicated;
    private int behaviorReportId;

    public RfnDevice getMeter() {
        return meter;
    }

    public void setMeter(RfnDevice meter) {
        this.meter = meter;
    }

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
}
