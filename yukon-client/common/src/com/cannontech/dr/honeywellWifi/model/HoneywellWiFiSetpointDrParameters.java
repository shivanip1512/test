package com.cannontech.dr.honeywellWifi.model;

import org.joda.time.Instant;

public class HoneywellWiFiSetpointDrParameters {
    private final int programId;
    private final int groupId;
    private final boolean tempOptionHeat;
    private final boolean optional;
    private final int tempOffset;
    private final int preTempOffset;
    private final Instant startTime;
    private final Instant stopTime;

    public HoneywellWiFiSetpointDrParameters(int programId, int groupId, boolean tempOptionHeat, boolean optional, int tempOffset,
            int preTempOffset, Instant startTime, Instant stopTime) {
        this.programId = programId;
        this.groupId = groupId;
        this.tempOptionHeat = tempOptionHeat;
        this.optional = optional;
        this.tempOffset = tempOffset;
        this.preTempOffset = preTempOffset;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public int getProgramId() {
        return programId;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean istempOptionHeat() {
        return tempOptionHeat;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getTempOffset() {
        return tempOffset;
    }

    public int getPreTempOffset() {
        return preTempOffset;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    @Override
    public String toString() {
        return "HoneywellWiFiSetpointDrParameters [programId=" + programId + ", groupId=" + groupId + ", tempOptionHeat="
                + tempOptionHeat + ", optional=" + optional + ", tempOffset=" + tempOffset + ", preTempOffset=" + preTempOffset
                + ", startTime=" + startTime + ", stopTime=" + stopTime + "]";
    }

}
