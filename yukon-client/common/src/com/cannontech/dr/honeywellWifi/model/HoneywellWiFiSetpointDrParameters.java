package com.cannontech.dr.honeywellWifi.model;

import org.joda.time.Instant;

public class HoneywellWiFiSetpointDrParameters {
    private final int programId;
    private final int eventId;
    private final int groupId;
    private final boolean tempOptionHeat;
    private final boolean optional;
    private final Double tempOffsetC;
    private final Instant startTime;
    private final Instant stopTime;
    private final int durationSeconds;

    public HoneywellWiFiSetpointDrParameters(int programId, int eventId, int groupId, boolean tempOptionHeat, boolean optional,
            Double tempOffsetC, Instant startTime, Instant stopTime, int durationSeconds) {
        this.programId = programId;
        this.eventId = eventId;
        this.groupId = groupId;
        this.tempOptionHeat = tempOptionHeat;
        this.optional = optional;
        this.tempOffsetC = tempOffsetC;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.durationSeconds = durationSeconds;
    }

    public int getProgramId() {
        return programId;
    }

    public int getEventId() {
        return eventId;
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

    public Double getTempOffsetC() {
        return tempOffsetC;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public String toString() {
        return "HoneywellWiFiSetpointDrParameters [programId=" + programId + ", eventId=" + eventId + ", groupId=" + groupId
                + ", tempOptionHeat=" + tempOptionHeat + ", optional=" + optional + ", tempOffsetC=" + tempOffsetC + ", startTime="
                + startTime + ", stopTime=" + stopTime + ", durationSeconds=" + durationSeconds + "]";
    }

}
