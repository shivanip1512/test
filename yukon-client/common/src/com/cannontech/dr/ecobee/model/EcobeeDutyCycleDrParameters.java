package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response event in Ecobee.
 */
public final class EcobeeDutyCycleDrParameters {
    private final Instant startTime;
    private final Instant endTime;
    private final int dutyCyclePercent;
    private final int randomTimeSeconds;
    private final boolean isOptional;
    private final int groupId;
    
    public EcobeeDutyCycleDrParameters(Instant startTime, Instant endTime, int dutyCyclePercent, int randomTimeSeconds, 
                                        boolean isOptional, int groupId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyCyclePercent = dutyCyclePercent;
        this.randomTimeSeconds = randomTimeSeconds;
        this.groupId = groupId;
        this.isOptional = isOptional;
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
