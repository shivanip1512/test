package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response event in Ecobee.
 */
public final class EcobeeSetpointDrParameters {
    private final int groupId;
    private final boolean tempOptionHeat;
    private final boolean optional;
    private final int tempOffset;
    private final Instant startTime;
    private final Instant stopTime;

    public EcobeeSetpointDrParameters(int groupId, boolean tempOptionHeat, boolean optional, int tempOffset, Instant startTime,
            Instant stopTime) {
        this.groupId = groupId;
        this.tempOptionHeat = tempOptionHeat;
        this.optional = optional;
        this.tempOffset = tempOffset;
        this.startTime = startTime;
        this.stopTime = stopTime;
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

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    @Override
    public String toString() {
        return "EcobeeSetpointDrParameters [groupId=" + groupId + ", tempOptionHeat=" + tempOptionHeat + ", optional=" + optional
                + ", tempOffset=" + tempOffset + ", startTime=" + startTime + ", stopTime=" + stopTime + "]";
    }

}
