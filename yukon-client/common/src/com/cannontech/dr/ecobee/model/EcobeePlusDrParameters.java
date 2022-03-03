package com.cannontech.dr.ecobee.model;

import java.io.Serializable;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a eco+ demand response event in Ecobee.
 */
public final class EcobeePlusDrParameters implements Serializable {
    private final int programId;
    private final int groupId;
    private final Instant startTime;
    private final Instant endTime;
    private final int randomTimeSeconds;
    private final boolean heatingEvent;

    public EcobeePlusDrParameters(int programId, int groupId, Instant startTime, Instant endTime, int randomTimeSeconds,
            boolean heatingEvent) {
        this.programId = programId;
        this.groupId = groupId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.randomTimeSeconds = randomTimeSeconds;
        this.heatingEvent = heatingEvent;
    }

    public int getProgramId() {
        return programId;
    }

    public int getGroupId() {
        return groupId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public int getRandomTimeSeconds() {
        return randomTimeSeconds;
    }

    public boolean isHeatingEvent() {
        return heatingEvent;
    }

}
