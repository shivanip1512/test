package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

/**
 * Contains all parameters required to initiate a eco+ demand response event in Ecobee.
 */
public final class EcobeePlusDrParameters {
    private final int groupId;
    private final Instant startTime;
    private final Instant endTime;
    private final int randomTimeSeconds;
    private final boolean heatingEvent;

    public EcobeePlusDrParameters(int groupId, Instant startTime, Instant endTime, int randomTimeSeconds, boolean heatingEvent) {
        this.groupId = groupId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.randomTimeSeconds = randomTimeSeconds;
        this.heatingEvent = heatingEvent;
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
