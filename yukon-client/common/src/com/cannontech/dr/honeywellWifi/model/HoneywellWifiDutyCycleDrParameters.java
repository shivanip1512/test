package com.cannontech.dr.honeywellWifi.model;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a duty cycle demand response
 * event for HoneywellWifi.
 */
public class HoneywellWifiDutyCycleDrParameters {
    private final int programId;
    private final int eventId;
    private final Instant startTime;
    private final Instant endTime;
    private final boolean optional;
    private final int dutyCyclePercent;
    private final int randomizationInterval;
    private final int groupId;
    private final int durationSeconds;

    public HoneywellWifiDutyCycleDrParameters(int programId, int eventId, Instant startTime, Instant endTime, boolean optional,
            int dutyCyclePercent, int randomizationInterval, int groupId, int durationSeconds) {
        this.programId = programId;
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.optional = optional;
        this.dutyCyclePercent = dutyCyclePercent;
        this.randomizationInterval = randomizationInterval;
        this.groupId = groupId;
        this.durationSeconds = durationSeconds;
    }

    public int getEventId() {
        return eventId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getDutyCyclePercent() {
        return dutyCyclePercent;
    }

    public int getRandomizationInterval() {
        return randomizationInterval;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getProgramId() {
        return programId;
    }

    @Override
    public String toString() {
        return "HoneywellWifiDutyCycleDrParameters [programId=" + programId + ", eventId=" + eventId + ", startTime=" + startTime
                + ", endTime=" + endTime + ", optional=" + optional + ", dutyCyclePercent=" + dutyCyclePercent
                + ", randomizationInterval=" + randomizationInterval + ", groupId=" + groupId + ", durationSeconds="
                + durationSeconds + "]";
    }

}
