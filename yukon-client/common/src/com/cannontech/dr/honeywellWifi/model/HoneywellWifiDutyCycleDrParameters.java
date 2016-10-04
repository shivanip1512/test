package com.cannontech.dr.honeywellWifi.model;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response event for HoneywellWifi.
 */
public class HoneywellWifiDutyCycleDrParameters {
    private final Instant startTime;
    private final Instant endTime;
    private final int dutyCyclePercent;
    private final boolean rampIn;
    private final boolean rampOut;
    private final int groupId;

    public HoneywellWifiDutyCycleDrParameters(Instant startTime, Instant endTime, int dutyCyclePercent, boolean rampIn,
            boolean rampOut, int groupId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyCyclePercent = dutyCyclePercent;
        this.rampIn = rampIn;
        this.rampOut = rampOut;
        this.groupId = groupId;
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

    public int getGroupId() {
        return groupId;
    }

}
