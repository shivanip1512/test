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
    private final boolean isMandatory;
    private final int groupId;

    public EcobeeDutyCycleDrParameters(int programId, Instant startTime, Instant endTime, int dutyCyclePercent,
            int randomTimeSeconds, boolean isMandatory, int groupId) {
        this.programId = programId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyCyclePercent = dutyCyclePercent;
        this.randomTimeSeconds = randomTimeSeconds;
        this.isMandatory = isMandatory;
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

    public boolean isMandatory() {
        return isMandatory;
    }

    public int getGroupId() {
        return groupId;
    }

}
