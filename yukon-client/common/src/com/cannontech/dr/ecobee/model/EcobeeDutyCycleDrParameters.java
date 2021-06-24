package com.cannontech.dr.ecobee.model;

import java.io.Serializable;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response event in Ecobee.
 */
public final class EcobeeDutyCycleDrParameters implements Serializable {
    private final int programId;
    private final Instant startTime;
    private final Instant endTime;
    private final int dutyCyclePercent;
    private final int randomTimeSeconds;
    private final boolean isOptional;
    private final int groupId;

    public EcobeeDutyCycleDrParameters(int programId, Instant startTime, Instant endTime, int dutyCyclePercent,
            int randomTimeSeconds, boolean isOptional, int groupId) {
        this.programId = programId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyCyclePercent = dutyCyclePercent;
        this.randomTimeSeconds = randomTimeSeconds;
        this.isOptional = isOptional;
        this.groupId = groupId;
    }

    public int getProgramId() {
        return programId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public int getDutyCyclePercent() {
        return dutyCyclePercent;
    }

    public int getRandomTimeSeconds() {
        return randomTimeSeconds;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public int getGroupId() {
        return groupId;
    }

}
