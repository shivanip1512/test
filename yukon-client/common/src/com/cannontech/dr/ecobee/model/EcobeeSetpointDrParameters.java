package com.cannontech.dr.ecobee.model;

import java.io.Serializable;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response event in Ecobee.
 */
public final class EcobeeSetpointDrParameters implements Serializable {
    private final int programId;
    private final int groupId;
    private final boolean tempOptionHeat;
    private final boolean optional;
    private final int tempOffset;
    private final Instant startTime;
    private final Instant stopTime;

    public EcobeeSetpointDrParameters(int programId, int groupId, boolean tempOptionHeat, boolean optional, int tempOffset,
            Instant startTime, Instant stopTime) {
        this.programId = programId;
        this.groupId = groupId;
        this.tempOptionHeat = tempOptionHeat;
        this.optional = optional;
        this.tempOffset = tempOffset;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public int getProgramId() {
        return programId;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isTempOptionHeat() {
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
        return "EcobeeSetpointDrParameters [programId=" + programId + ", groupId=" + groupId + ", tempOptionHeat="
                + tempOptionHeat + ", optional=" + optional + ", tempOffset=" + tempOffset + ", startTime=" + startTime
                + ", stopTime=" + stopTime + "]";
    }

}
