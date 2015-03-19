package com.cannontech.capcontrol.model;

import org.joda.time.Instant;

import com.cannontech.common.model.Phase;

public class RegulatorEvent {

    protected RegulatorEvent(int regulatorId, Instant timestamp, EventType type, Phase phase, String userName) {
        this.regulatorId = regulatorId;
        this.timestamp = timestamp;
        this.type = type;
        this.phase = phase;
        this.userName = userName;
    }

    public static RegulatorEvent of(int regulatorId, Instant timestamp, EventType type, Phase phase, String userName) {
        return new RegulatorEvent(regulatorId, timestamp, type, phase, userName);
    }

    public static enum EventType {
        TAP_UP,
        TAP_DOWN,
        SCAN,
        ;
    }

    private final int regulatorId;
    private final Instant timestamp;
    private final EventType type;
    private final Phase phase;
    private final String userName;


    public int getRegulatorId() {
        return regulatorId;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public EventType getType() {
        return type;
    }
    public Phase getPhase() {
        return phase;
    }
    public String getUserName() {
        return userName;
    }

}
