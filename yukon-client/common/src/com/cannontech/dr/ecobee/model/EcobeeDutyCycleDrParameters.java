package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response event in Ecobee.
 */
public final class EcobeeDutyCycleDrParameters {
    private final Instant startTime;
    private final Instant endTime;
    private final int dutyCyclePercent;
    private final boolean rampIn;
    private final boolean rampOut;
    private final boolean isOptional;
    private final int groupId;
    
    public EcobeeDutyCycleDrParameters(Instant startTime, Instant endTime, int dutyCyclePercent, boolean rampIn, 
                                       boolean rampOut, boolean isOptional, int groupId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyCyclePercent = dutyCyclePercent;
        this.rampIn = rampIn;
        this.rampOut = rampOut;
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

    public boolean isRampIn() {
        return rampIn;
    }

    public boolean isRampOut() {
        return rampOut;
    }
    
    public boolean isOptional() {
        return isOptional;
    }

    public int getGroupId() {
        return groupId;
    }
}
